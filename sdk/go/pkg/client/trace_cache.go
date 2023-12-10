package client

import (
	gocache "github.com/patrickmn/go-cache"
	"github.com/wirequery/wirequery/sdk/go/pkg/evaluator"
	"time"
)

var cache = gocache.New(5*time.Second, 10*time.Second)

func PutCache(traceId string, value *evaluator.Context) {
	cache.Set(traceId, value, gocache.DefaultExpiration)
}

func GetCache(traceId string) *evaluator.Context {
	res, _ := cache.Get(traceId)
	return res.(*evaluator.Context)
}
