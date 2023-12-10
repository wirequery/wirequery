package client

import (
	"github.com/stretchr/testify/assert"
	"github.com/wirequery/wirequery/sdk/go/pkg/evaluator"
	"testing"
)

func TestPutCache_GetCache(t *testing.T) {
	context := evaluator.Context{}
	PutCache("", &context)
	assert.Equal(t, &context, GetCache(""))
}
