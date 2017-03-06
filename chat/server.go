package main

import (
	"net"
	"log"
	"fmt"
)

var (
	connections []net.Conn
)

func main()  {
	tpcListener, err := net.Listen("tcp", ":8080")
	if err != nil {
	}

	defer tpcListener.Close()
	for {
		tcpConnection, err := tpcListener.Accept()
		if err != nil {

		}
		connections = append(connections, tcpConnection)
		go handleConnection(tcpConnection)
	}
}

func handleConnection(conn net.Conn) {
	buffer := make([]byte, 4096)
	defer conn.Close()
	for {
		n, err := conn.Read(buffer)
		if err != nil || n == 0 {
			conn.Close()
			break
		}
		fmt.Println(string(buffer))
		for i := range connections {
			if connections[i] != conn {
				number, err := connections[i].Write(buffer)
				if err != nil || number == 0{
					log.Println(err)
				}
			}
		}
	}
	log.Printf("Connection from %v closed", conn.RemoteAddr())
}
