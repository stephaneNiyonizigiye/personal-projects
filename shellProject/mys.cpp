#include <vector>
#include <sstream>
#include <iostream>
#include <unistd.h>
#include <sys/wait.h>

using namespace std;

vector<char *> parseCommand(const string &input)
{
    vector<char *> args;
    stringstream ss(input);
    string token;

    while (ss >> token)
    {
        char *arg = new char[token.size() + 1];
        strcpy(arg, token.c_str());
        args.push_back(arg);
    }
    args.push_back(nullptr); // end signal for execvp
    return args;
}
int main()
{
    string input;

    while (true)
    {
        cout << "mysh>";
        getline(cin, input);

        if (input.empty())
            continue;
        if (input == "exit")
            break;

        pid_t pid = fork();

        if (pid == 0)
        // child process
        {
            vector<char *> args = parseCommand(input);
            execvp(args[0], args.data());
            perror("execvp failed");
            exit(1);
        }
        else if (pid > 0)
        {
            // parent process
            wait(nullptr); // wait for parent process
        }
        else
        {
            perror("fork failed");
        }
    }
}