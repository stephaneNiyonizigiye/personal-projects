terraform {

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.0"
    }
  }
  required_version = ">= 1.0.0"


  backend "s3" {
    bucket         = "bucket-for-terraform-state"
    key            = "path/to/my/terraform.tfstate"
    region         = "us-west-2"
    dynamodb_table = "terraform-lock-table"
    encrypt        = true

  }

}

provider "aws" {

  default_tags {
    tags = {
      Owner       = "Stephane Niyonizigiye"
      Environment = "Development"
      Project     = "Minimalistic Application"
    }
  }
  region = "us-west-2"
}

# Create a VPC(Virtual Private Cloud)
resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"
  tags = {
    Name = "main_vpc"
  }
}

# Create a Subnet
resource "aws_subnet" "main" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.1.0/24"
  availability_zone = "us-west-2a"
  tags = {
    Name = "main_subnet"
  }
}

# Create an Internet Gateway
resource "aws_internet_gateway" "gw" {
  vpc_id = aws_vpc.main.id
  tags = {
    Name = "main_igw"
  }
}

# Create a Route Table
resource "aws_route_table" "r" {
  vpc_id = aws_vpc.main.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.gw.id
  }
  tags = {
    Name = "main_route_table"
  }
}

# Associate the Route Table with the Subnet
resource "aws_route_table_association" "a" {
  subnet_id      = aws_subnet.main.id
  route_table_id = aws_route_table.r.id
}

#Security Group to allow inbound HTTPS traffic
resource "aws_security_group" "sg" {
  name        = "allow_http"
  description = "Allow HTTP inbound traffic"
  vpc_id      = aws_vpc.main.id

  tags = {
    Name = "allow_http_sg"
  }
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}


# Create an EC2 instance
resource "aws_instance" "ec2" {
  ami                    = "ami-0c55b159cbfafe1f0" # Amazon Linux 2 AMI (HVM), SSD Volume Type
  instance_type          = "t2.micro"
  subnet_id              = aws_subnet.main.id
  vpc_security_group_ids = [aws_security_group.sg.id]

  tags = {
    Name = "web_instance"
  }

  user_data = <<-EOF
              #!/bin/bash
              yum install -y httpd
              systemctl start httpd
              systemctl enable httpd
              echo "Hello , new application" > /var/www/html/index.html
            EOF

}

#Create RDS instance
resource "aws_db_subnet_group" "db_subnet" {
  name       = "main_db_subnet"
  subnet_ids = [aws_subnet.main.id]

  tags = {
    Name = "main_db_subnet"
  }
}
resource "aws_db_instance" "db" {
  allocated_storage      = 20
  engine                 = "mysql"
  engine_version         = "8.0"
  instance_class         = "db.t2.micro"
  username               = var.db_username
  password               = var.db_password
  db_name                = "mydb"
  parameter_group_name   = "default.mysql8.0"
  skip_final_snapshot    = true
  vpc_security_group_ids = [aws_security_group.sg.id]
  db_subnet_group_name   = aws_db_subnet_group.db_subnet.name
}
variable "db_username" {
  description = "The username for the RDS instance"
  type        = string
}
variable "db_password" {
  description = "The password for the RDS instance"
  type        = string
  sensitive   = true
}
#Create S3 bucket
resource "aws_s3_bucket" "b" {
  bucket = "my-unique-bucket-name-123456"
  tags = {
    Name = "my_s3_bucket"
  }
}


