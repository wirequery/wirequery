package client

import (
	"github.com/google/cel-go/cel"
	"github.com/wirequery/wirequery/sdk/go/pkg/evaluator"
	. "github.com/wirequery/wirequery/sdk/go/pkg/wirequerypb"
)

func (w *WireQueryClient) handleAddQuery(env *cel.Env, choice *QueryMutation_AddQuery) {
	query, err := CompileQuery(choice.AddQuery, env)
	if err != nil {
		_ = w.ReportError(choice.AddQuery.QueryId, err.Error())
		return
	}
	println("Added Query: " + query.QueryId)
	defer w.mutex.Unlock()
	w.mutex.Lock()
	w.queries = append(w.queries, *query)
}

func (w *WireQueryClient) handleRemoveQueryById(choice *QueryMutation_RemoveQueryById) {
	var newQueries []evaluator.CompiledQuery
	println("Removed Query: " + choice.RemoveQueryById)
	for _, query := range w.queries {
		if choice.RemoveQueryById != query.QueryId {
			newQueries = append(newQueries, query)
		}
	}
	w.queries = newQueries
}
