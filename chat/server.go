package main

import (
	"net"
	"log"
	"fmt"
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

// TODO: fix non-clearing buffer (after couple messages, clients send too much, or server messes up - to new messages
// TODO: content of older messages is appended

// TODO: fix error in client when server fails
func main()  {
	tpcListener, err := net.Listen("tcp", ADDRESS)
	if err != nil {
		log.Fatal(err)
	}
	buffer := make([]byte, BUFFER_SIZE)
	defer tpcListener.Close()
	for {
		tcpConnection, err := tpcListener.Accept()
		if err != nil {
			log.Fatal(err)
		}
		n, err := tcpConnection.Read(buffer)
		if err != nil || n == 0 {
			tcpConnection.Close()
			log.Fatal(err)
		}
		nickname_value := string(buffer)
		client := Client{
			nickname : nickname_value,
			connection: tcpConnection,
		}
		connections = append(connections, client)
		go handleConnection(client)
	}
}

func handleConnection(client Client) {
	buffer := make([]byte, BUFFER_SIZE)
	defer client.connection.Close()
	for {
		n, err := client.connection.Read(buffer)
		if err != nil || n == 0 {
			client.connection.Close()
			log.Fatal(err)
		}
		fmt.Printf("%s>> %s \n", client.nickname, string(buffer))
		for i := range connections {
			if connections[i].connection != client.connection {
				_, err := connections[i].connection.Write(buffer)
				if err != nil {
					log.Fatal(err)
				}
			}
		}
	}
	log.Printf("Connection from %v closed ", client.connection.RemoteAddr())
}
