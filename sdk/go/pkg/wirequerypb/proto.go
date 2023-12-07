//go:generate protoc --go-grpc_out=. --go_out=.. --go-grpc_opt=paths=source_relative -I=../../../../proto wirequery.proto

package wirequerypb
