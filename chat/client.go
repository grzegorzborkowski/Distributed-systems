package main

import (
	"net"
	"os"
	"bufio"
	"fmt"
)

func main() {
	tcpConnection, err := net.Dial("tcp", ":8080")
	if err != nil {
		fmt.Println("error")
	}
	fmt.Println("Nie ma errora")
	go write(tcpConnection)
	read(tcpConnection)
}

func read(conn net.Conn) {
	buffer := make([]byte, 4096)
	for {
		n, err := conn.Read(buffer)
		if err != nil || n == 0 {
			conn.Close()
		}
		fmt.Print("Received following message: ", string(buffer[0:n]))
	}
}

func write(conn net.Conn) {
	reader := bufio.NewReader(os.Stdin)
	for {
		text, _ := reader.ReadString('\n')
		conn.Write([]byte(text))
	}
}