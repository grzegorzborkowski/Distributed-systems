package main

import (
	"net"
	"log"
	"fmt"
	"reflect"
)

const (
	ADDRESS = "127.0.0.1:8080"
	BUFFER_SIZE = 4096
)

var (
	tcpConnections []Client
	udpAdresses []*net.UDPAddr
)

type Client struct {
	nickname string
	connection net.Conn
}

// TODO: add sending UDP messages from server to client
// TODO: add UDP multicast messages
func main()  {
	tcpListener, err := net.Listen("tcp", ADDRESS)
	if err != nil {
		log.Fatal(err)
	}
	udpAddress, err := net.ResolveUDPAddr("udp", ADDRESS)
	if err != nil {
		log.Fatal(err)
	}
	udpConnection, err := net.ListenUDP("udp4", udpAddress)
	if err != nil {
		log.Fatal(err)
	}
	defer func() {
		udpConnection.Close()
	}()
	go handleUDPConnection(*udpConnection)
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
			tcpConnections = append(tcpConnections, client)
			go handleConnection(client)
		}
	}
}

func handleUDPConnection(udpConnection net.UDPConn) {
	buffer := make([]byte, BUFFER_SIZE)
	for {
		_, UDPaddress, err := udpConnection.ReadFromUDP(buffer)
		if err != nil {
			log.Fatal(err)
		}
		contains_result := contains(udpAdresses, UDPaddress)
		if !contains_result {
			udpAdresses = append(udpAdresses, UDPaddress)
		}
		fmt.Print(string(buffer))
		go sendUDPDataToClients(buffer, UDPaddress, udpConnection)
		buffer = make([]byte, BUFFER_SIZE)
	}
}

func sendUDPDataToClients(buffer []byte, UDPaddress *net.UDPAddr, udpConnection net.UDPConn) {
	for i := range udpAdresses {
		if !reflect.DeepEqual(UDPaddress, udpAdresses[i]) {
			_, err := udpConnection.WriteToUDP(buffer, udpAdresses[i])
			if err != nil {
				log.Fatal(err)
			}
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
		buffer = append([]byte(client.nickname), buffer...)
		for i := range tcpConnections {
			if tcpConnections[i].connection != client.connection {
				_, err := tcpConnections[i].connection.Write(buffer)
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
	for i := range tcpConnections {
		if tcpConnections[i].connection == client.connection {
			tcpConnections = append(tcpConnections[:i], tcpConnections[i+1:]...)
			break
		}
	}
	client.connection.Close()
}

func contains(s []*net.UDPAddr, e *net.UDPAddr) bool {
	for _, a := range s {
		if reflect.DeepEqual(a, e) {
			return true
		}
	}
	return false
}