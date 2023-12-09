package client

import (
	"context"
	"crypto/tls"
	"github.com/wirequery/wirequery/sdk/go/pkg/evaluator"
	. "github.com/wirequery/wirequery/sdk/go/pkg/wirequerypb"
	"google.golang.org/grpc"
	"google.golang.org/grpc/backoff"
	"google.golang.org/grpc/credentials"
	"log"
	"sync"
	"time"
)

type WireQueryClient struct {
	ctx     context.Context
	client  WirequeryServiceClient
	mutex   sync.Mutex
	queries []evaluator.CompiledQuery
	apiKey  string
	appName string
}

func Listen(host string, appName string, apiKey string) *WireQueryClient {
	conn := dialWireQueryManager(host)
	client := NewWirequeryServiceClient(conn)
	wireQueryClient := WireQueryClient{ctx: context.Background(), client: client, apiKey: apiKey, appName: appName}
	go wireQueryClient.listenForIncomingQueries(client, appName, apiKey)
	return &wireQueryClient
}

func dialWireQueryManager(wireQueryServer string) *grpc.ClientConn {
	var opts []grpc.DialOption
	opts = append(opts,
		grpc.WithBlock(),
		grpc.WithTransportCredentials(credentials.NewTLS(&tls.Config{InsecureSkipVerify: false})),
		grpc.WithConnectParams(grpc.ConnectParams{
			Backoff: backoff.Config{
				BaseDelay:  1 * time.Second,
				Multiplier: 1.6,
				MaxDelay:   15 * time.Second,
			}}),
	)
	conn, err := grpc.Dial(wireQueryServer, opts...)
	if err != nil {
		log.Fatalf("Failed to dial: %v", err)
	}
	return conn
}

func (w *WireQueryClient) listenForIncomingQueries(client WirequeryServiceClient, appName string, apiKey string) {
	for {
		env, _ := evaluator.CreateCelEnv()
		println("Listening for queries...")
		w.queries = []evaluator.CompiledQuery{}
		request := ListenForQueriesRequest{AppName: appName, ApiKey: apiKey}
		stream, err := client.ListenForQueries(w.ctx, &request)
		for {
			if err != nil {
				println("Reconnecting...")
				break
			}
			in, err := stream.Recv()
			if err != nil {
				println("An error occurred! Reconnecting...")
				break
			}

			switch choice := in.QueryMutationChoice.(type) {
			case *QueryMutation_AddQuery:
				w.handleAddQuery(env, choice)
			case *QueryMutation_RemoveQueryById:
				w.handleRemoveQueryById(choice)
			}
		}
	}
}

func (w *WireQueryClient) GetQueries() []evaluator.CompiledQuery {
	println(w.queries)
	return w.queries
}
