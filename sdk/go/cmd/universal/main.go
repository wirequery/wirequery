package main

import (
	"github.com/gin-gonic/gin"
	"github.com/wirequery/wirequery/sdk/go/pkg/client"
	"github.com/wirequery/wirequery/sdk/go/pkg/evaluator"
	"net/http"
)

func main() {
	router := gin.Default()
	wqsClient := client.Listen(
		"grpc.wirequery.io:443",
		"transactions",
		"1/0df82d0b-8dca-4002-8f47-0d210ab1c734",
	)
	v1 := router.Group("/api/v1")
	{
		v1.POST("/events", func(context *gin.Context) {
			queries := wqsClient.GetQueries()
			body := evaluator.Context{}
			if err := context.BindJSON(&body); err != nil {
				context.AbortWithError(http.StatusBadRequest, err)
				return
			}
			results, err := evaluator.Eval(&queries, body, nil)
			if err != nil {
				context.AbortWithError(http.StatusBadRequest, err)
				return
			}
			wqsClient.ReportResult(results)
		})
	}
	println("Listening to port 9888")
	err := router.Run(":9888")
	if err != nil {
		return
	}
}
