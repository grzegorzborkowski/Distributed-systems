package main

import (
	"net"
	"os"
	"bufio"
	"fmt"
	"log"
)

const (
	ADDRESS = "127.0.0.1:8080"
	BUFFER_SIZE = 4096
)

func main() {
	tcpConnection, err := net.Dial("tcp", ADDRESS)
	if err != nil {
		log.Fatal(err)
	}
	reader := bufio.NewReader(os.Stdin)
	log.Println("What's your name?")
	nickname, err := reader.ReadString('\n')
	if err != nil {
		log.Fatal(err)
	}
	tcpConnection.Write([]byte(nickname))
	go write(tcpConnection)
	read(tcpConnection)
}

func read(conn net.Conn) {
	buffer := make([]byte, BUFFER_SIZE)
	for {
		n, err := conn.Read(buffer)
		if err != nil || n == 0 {
			log.Print("Server is down")
			conn.Close()
			break
		}
		fmt.Print(string(buffer[0:n-1]))
	}
}

func write(conn net.Conn) {
	reader := bufio.NewReader(os.Stdin)
	for {
		text, err := reader.ReadString('\n')
		if err != nil {
			log.Fatal(err)
		}
		conn.Write([]byte(text))
	}
}