package client

import (
	"github.com/stretchr/testify/assert"
	"github.com/wirequery/wirequery/sdk/go/pkg/evaluator"
	"testing"
)

func TestPutCache_GetCache(t *testing.T) {
	t.Run("puts and gets from cache", func(t *testing.T) {
		context := evaluator.Context{}
		PutCache("123", &context)
		assert.Equal(t, &context, GetCache("123"))
	})

	t.Run("returns nil if entry not in cache", func(t *testing.T) {
		context := evaluator.Context{}
		PutCache("123", &context)
		assert.Nil(t, GetCache("456"))
	})
}
