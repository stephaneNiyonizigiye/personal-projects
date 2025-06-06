#include <iostream>
#include <unistd.h>
#include <string.h>
#include <pthread.h>
#include <vector>
#include <netinet/in.h>
#include <sys/socket.h>

#define PORT 5458
#define MAX_CLIENTS 10

using namespace std;

vector<int> clients; // keep track of connected clients
pthread_mutex_t client_mutex = PTHREAD_MUTEX_INITIALIZER;

void *handleClient(void *socketDesc)
{
    int clientSocket = *(int *)socketDesc;
    char buffer[1024];
    string welcome = "########################################\n#  Welcome to the chat server !         #\n########################################\n";
    send(clientSocket, welcome.c_str(), welcome.size(), 0);

    while (true)
    {
        memset(buffer, 0, sizeof(buffer));
        int bytesReceived = recv(clientSocket, buffer, sizeof(buffer), 0);
        if (bytesReceived <= 0)
        {
            cout << "Client connected !" << endl;
            break;
        }

        cout << "Received: " << buffer;

        // response to Echo
        string response = "Received message : ";
        response += buffer;
        send(clientSocket, response.c_str(), response.size(), 0);
    }

    close(clientSocket);

    // Supprimer le client de la liste
    pthread_mutex_lock(&client_mutex);
    clients.erase(remove(clients.begin(), clients.end(), clientSocket), clients.end());
    pthread_mutex_unlock(&client_mutex);

    pthread_exit(nullptr);
}

int main()
{
    int serverSocket, newSocket;
    struct sockaddr_in serverAddr, clientAddr;
    socklen_t addrLen = sizeof(clientAddr);

    serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (serverSocket == -1)
    {
        cerr << "# Error while creating socket #" << endl;
        return 1;
    }

    serverAddr.sin_family = AF_INET;
    serverAddr.sin_addr.s_addr = INADDR_ANY;
    serverAddr.sin_port = htons(PORT);

    if (bind(serverSocket, (struct sockaddr *)&serverAddr, sizeof(serverAddr)) < 0)
    {
        cerr << "# Binding error #" << endl;
        return 1;
    }

    if (listen(serverSocket, MAX_CLIENTS) < 0)
    {
        cerr << "# Listening error #" << endl;
        return 1;
    }

    cout << "# Server started on the port #" << PORT << endl;

    while (true)
    {
        newSocket = accept(serverSocket, (struct sockaddr *)&clientAddr, &addrLen);
        if (newSocket < 0)
        {
            cerr << "# Erreur d'accept #" << endl;
            continue;
        }

        cout << "New client is connected!" << endl;

        pthread_t threadId;
        pthread_mutex_lock(&client_mutex);
        clients.push_back(newSocket);
        pthread_mutex_unlock(&client_mutex);

        if (pthread_create(&threadId, nullptr, handleClient, (void *)&clients.back()) != 0)
        {
            cerr << "# Error while creating the thread #" << endl;
        }

        pthread_detach(threadId);
    }

    close(serverSocket);
    return 0;
}
