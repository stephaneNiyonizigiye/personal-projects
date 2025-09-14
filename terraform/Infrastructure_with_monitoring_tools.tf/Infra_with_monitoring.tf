terraform {
  #AWS provider
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.0"
    }

  }
}
data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }
}

provider "aws" {
  region = "us-east-1"
}
# Create a VPC
resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"
  tags = {
    Name = "main_vpc"
  }
}
# Create a public subnet
resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.0.0/24"
  map_public_ip_on_launch = true
  availability_zone       = "us-east-1a"
  tags = {
    Name = "public_subnet"
  }
}
# Create an internet gateway
resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.main.id
  tags = {
    Name = "main_igw"
  }
}
# Create a route table
resource "aws_route_table" "public_rt" {
  vpc_id = aws_vpc.main.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }
  tags = {
    Name = "public_rt"
  }
}
# Associate the route table with the public subnet
resource "aws_route_table_association" "public_assoc" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public_rt.id
}
# Create a security group
resource "aws_security_group" "web_sg" {
  vpc_id = aws_vpc.main.id
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = {
    Name = "web_sg"
  }
  ingress {
    from_port   = 9200
    to_port     = 9200
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 5601
    to_port     = 5601
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

}

# Create a CloudWatch Log Group
resource "aws_cloudwatch_log_group" "web_log_group" {
  name              = "/aws/ec2/web_instance"
  retention_in_days = 7
}
# Create a CloudWatch Log Stream
resource "aws_cloudwatch_log_stream" "web_log_stream" {
  name           = "web_instance_log_stream"
  log_group_name = aws_cloudwatch_log_group.web_log_group.name
}
# IAM Role for EC2 to write logs to CloudWatch
resource "aws_iam_role" "ec2_role" {
  name = "ec2_cloudwatch_role"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "ec2.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF
}

# Attach policy to the IAM Role
resource "aws_iam_role_policy" "ec2_policy" {
  name   = "ec2_cloudwatch_policy"
  role   = aws_iam_role.ec2_role.id
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ],
      "Resource": "*"
    }
  ]
}
EOF
}
# Instance Profile for the EC2 instance 
resource "aws_iam_instance_profile" "ec2_instance_profile" {
  name = "ec2_instance_profile"
  role = aws_iam_role.ec2_role.name
}

# Create an EC2 instance
resource "aws_instance" "web" {
  ami                    = data.aws_ami.amazon_linux.id
  instance_type          = "t2.micro"
  subnet_id              = aws_subnet.public.id
  vpc_security_group_ids = [aws_security_group.web_sg.id]

  tags = {
    Name = "web_instance"
  }

  user_data = <<-EOF
              #!/bin/bash
              yum update -y
              yum install -y httpd
              systemctl start httpd
              systemctl enable httpd
              echo "<h1>Hello from Terraform</h1>" > /var/www/html/index.html
              EOF
}


# Output the public IP of the instance
output "instance_public_ip" {
  value = aws_instance.web.public_ip
}

#ELK Stack
resource "aws_instance" "elk" {
  ami                    = data.aws_ami.amazon_linux.id # Amazon Linux 2 AMI
  instance_type          = "t3.large"
  subnet_id              = aws_subnet.public.id
  vpc_security_group_ids = [aws_security_group.web_sg.id]
  iam_instance_profile   = aws_iam_instance_profile.ec2_instance_profile.name
  tags = {
    Name = "elk_instance"
  }
  user_data = <<-EOF
                #!/bin/bash
                yum update -y
                # Install Java (prerequisite for ELK)
                amazon-linux-extras install java-openjdk11 -y
                # Download and install Elasticsearch
                wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-7.10.0-x86_64.rpm
                rpm --install elasticsearch-7.10.0-x86_64.rpm
                systemctl enable elasticsearch
                systemctl start elasticsearch
                # Download and install Logstash
                wget https://artifacts.elastic.co/downloads/logstash/logstash-7.10.0.rpm
                rpm --install logstash-7.10.0.rpm
                systemctl enable logstash
                systemctl start logstash
                # Download and install Kibana
                wget https://artifacts.elastic.co/downloads/kibana/kibana-7.10.0-x86_64.rpm
                rpm --install kibana-7.10.0-x86_64.rpm

                systemctl enable kibana
                systemctl start kibana
                EOF
}



# Output the public IP of the ELK instance
output "elk_instance_public_ip" {
  value = aws_instance.elk.public_ip
}


