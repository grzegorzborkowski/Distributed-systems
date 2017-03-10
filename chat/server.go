package main

import (
	"net"
	"log"
)

const (
	ADDRESS = "127.0.0.1:8080"
	BUFFER_SIZE = 4096
)

var (
	connections []Client
)

type Client struct {
	nickname string
	connection net.Conn
}

func main()  {
	tcpListener, err := net.Listen("tcp", ADDRESS)
	if err != nil {
		log.Fatal(err)
	}
	buffer := make([]byte, BUFFER_SIZE)
	defer func() {
		tcpListener.Close()
	}()
	for {
		tcpConnection, err := tcpListener.Accept()
		if err != nil {
			log.Fatal(err)
		}
		n, _ := tcpConnection.Read(buffer)
		if n != 0 {
			nickname_value := string(buffer)
			client := Client{
			nickname : nickname_value,
				connection: tcpConnection,
			}
			connections = append(connections, client)
			go handleConnection(client)
		}
	}
}

func handleConnection(client Client) {
	buffer := make([]byte, BUFFER_SIZE)
	defer client.connection.Close()
	for {
		n, err := client.connection.Read(buffer)
		if err != nil || n == 0 {
			removeClient(client)
			break
		}
		log.Printf("%s>> %s \n", client.nickname, string(buffer))
		for i := range connections {
			if connections[i].connection != client.connection {
				_, err := connections[i].connection.Write(buffer)
				if err != nil {
					log.Fatal(err)
				}
			}
		}
		buffer = make([]byte, BUFFER_SIZE)
	}
	log.Printf("Connection from %v closed ", client.connection.RemoteAddr())
}

func removeClient(client Client) {
	for i := range connections {
		if connections[i].connection == client.connection {
			connections = append(connections[:i], connections[i+1:]...)
			break
		}
	}
	client.connection.Close()
}