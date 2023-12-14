package main

import (
	"flag"
	"github.com/gin-gonic/gin"
	"github.com/wirequery/wirequery/sdk/go/pkg/client"
	"github.com/wirequery/wirequery/sdk/go/pkg/evaluator"
	"net/http"
	"strconv"
)

var appName = flag.String("appName", "", "App name")
var apiKey = flag.String("apiKey", "", "Api key of app")
var port = flag.Int("port", 8091, "Port")
var host = flag.String("host", "grpc.wirequery.io:443", "App name")

func main() {
	flag.Parse()
	router := gin.Default()
	wqsClient := client.Listen(*host, *appName, *apiKey)
	v1 := router.Group("/api/v1")
	{
		v1.POST("/events", func(context *gin.Context) {
			queries := wqsClient.GetQueries()
			body := evaluator.Context{}
			if err := context.BindJSON(&body); err != nil {
				_ = context.AbortWithError(http.StatusBadRequest, err)
				return
			}
			client.PutCache(body.TraceId, &body)
			results, err := evaluator.Eval(&queries, body)
			if err != nil {
				_ = context.AbortWithError(http.StatusBadRequest, err)
				return
			}
			wqsClient.ReportResult(results)
		})
	}
	println("Listening to port " + strconv.Itoa(*port))
	err := router.Run(":" + strconv.Itoa(*port))
	if err != nil {
		return
	}
}
