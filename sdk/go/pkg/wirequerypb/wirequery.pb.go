// Code generated by protoc-gen-go. DO NOT EDIT.
// versions:
// 	protoc-gen-go v1.28.1
// 	protoc        v4.24.4
// source: wirequery.proto

package wirequerypb

import (
	protoreflect "google.golang.org/protobuf/reflect/protoreflect"
	protoimpl "google.golang.org/protobuf/runtime/protoimpl"
	reflect "reflect"
	sync "sync"
)

const (
	// Verify that this generated code is sufficiently up-to-date.
	_ = protoimpl.EnforceVersion(20 - protoimpl.MinVersion)
	// Verify that runtime/protoimpl is sufficiently up-to-date.
	_ = protoimpl.EnforceVersion(protoimpl.MaxVersion - 20)
)

type ListenForQueriesRequest struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	AppName string `protobuf:"bytes,1,opt,name=app_name,json=appName,proto3" json:"app_name,omitempty"`
	ApiKey  string `protobuf:"bytes,2,opt,name=api_key,json=apiKey,proto3" json:"api_key,omitempty"`
}

func (x *ListenForQueriesRequest) Reset() {
	*x = ListenForQueriesRequest{}
	if protoimpl.UnsafeEnabled {
		mi := &file_wirequery_proto_msgTypes[0]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *ListenForQueriesRequest) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*ListenForQueriesRequest) ProtoMessage() {}

func (x *ListenForQueriesRequest) ProtoReflect() protoreflect.Message {
	mi := &file_wirequery_proto_msgTypes[0]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use ListenForQueriesRequest.ProtoReflect.Descriptor instead.
func (*ListenForQueriesRequest) Descriptor() ([]byte, []int) {
	return file_wirequery_proto_rawDescGZIP(), []int{0}
}

func (x *ListenForQueriesRequest) GetAppName() string {
	if x != nil {
		return x.AppName
	}
	return ""
}

func (x *ListenForQueriesRequest) GetApiKey() string {
	if x != nil {
		return x.ApiKey
	}
	return ""
}

type QueryMutation struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	// Types that are assignable to QueryMutationChoice:
	//
	//	*QueryMutation_AddQuery
	//	*QueryMutation_RemoveQueryById
	//	*QueryMutation_QueryOneTrace
	QueryMutationChoice isQueryMutation_QueryMutationChoice `protobuf_oneof:"queryMutationChoice"`
}

func (x *QueryMutation) Reset() {
	*x = QueryMutation{}
	if protoimpl.UnsafeEnabled {
		mi := &file_wirequery_proto_msgTypes[1]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *QueryMutation) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*QueryMutation) ProtoMessage() {}

func (x *QueryMutation) ProtoReflect() protoreflect.Message {
	mi := &file_wirequery_proto_msgTypes[1]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use QueryMutation.ProtoReflect.Descriptor instead.
func (*QueryMutation) Descriptor() ([]byte, []int) {
	return file_wirequery_proto_rawDescGZIP(), []int{1}
}

func (m *QueryMutation) GetQueryMutationChoice() isQueryMutation_QueryMutationChoice {
	if m != nil {
		return m.QueryMutationChoice
	}
	return nil
}

func (x *QueryMutation) GetAddQuery() *Query {
	if x, ok := x.GetQueryMutationChoice().(*QueryMutation_AddQuery); ok {
		return x.AddQuery
	}
	return nil
}

func (x *QueryMutation) GetRemoveQueryById() string {
	if x, ok := x.GetQueryMutationChoice().(*QueryMutation_RemoveQueryById); ok {
		return x.RemoveQueryById
	}
	return ""
}

func (x *QueryMutation) GetQueryOneTrace() *QueryOneTrace {
	if x, ok := x.GetQueryMutationChoice().(*QueryMutation_QueryOneTrace); ok {
		return x.QueryOneTrace
	}
	return nil
}

type isQueryMutation_QueryMutationChoice interface {
	isQueryMutation_QueryMutationChoice()
}

type QueryMutation_AddQuery struct {
	AddQuery *Query `protobuf:"bytes,1,opt,name=add_query,json=addQuery,proto3,oneof"`
}

type QueryMutation_RemoveQueryById struct {
	RemoveQueryById string `protobuf:"bytes,2,opt,name=remove_query_by_id,json=removeQueryById,proto3,oneof"`
}

type QueryMutation_QueryOneTrace struct {
	QueryOneTrace *QueryOneTrace `protobuf:"bytes,3,opt,name=query_one_trace,json=queryOneTrace,proto3,oneof"`
}

func (*QueryMutation_AddQuery) isQueryMutation_QueryMutationChoice() {}

func (*QueryMutation_RemoveQueryById) isQueryMutation_QueryMutationChoice() {}

func (*QueryMutation_QueryOneTrace) isQueryMutation_QueryMutationChoice() {}

type Query struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	QueryId             string       `protobuf:"bytes,1,opt,name=query_id,json=queryId,proto3" json:"query_id,omitempty"`
	QueryHead           *QueryHead   `protobuf:"bytes,2,opt,name=queryHead,proto3" json:"queryHead,omitempty"`
	StreamOperations    []*Operation `protobuf:"bytes,3,rep,name=stream_operations,json=streamOperations,proto3" json:"stream_operations,omitempty"`
	AggregatorOperation *Operation   `protobuf:"bytes,4,opt,name=aggregator_operation,json=aggregatorOperation,proto3" json:"aggregator_operation,omitempty"`
}

func (x *Query) Reset() {
	*x = Query{}
	if protoimpl.UnsafeEnabled {
		mi := &file_wirequery_proto_msgTypes[2]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *Query) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*Query) ProtoMessage() {}

func (x *Query) ProtoReflect() protoreflect.Message {
	mi := &file_wirequery_proto_msgTypes[2]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use Query.ProtoReflect.Descriptor instead.
func (*Query) Descriptor() ([]byte, []int) {
	return file_wirequery_proto_rawDescGZIP(), []int{2}
}

func (x *Query) GetQueryId() string {
	if x != nil {
		return x.QueryId
	}
	return ""
}

func (x *Query) GetQueryHead() *QueryHead {
	if x != nil {
		return x.QueryHead
	}
	return nil
}

func (x *Query) GetStreamOperations() []*Operation {
	if x != nil {
		return x.StreamOperations
	}
	return nil
}

func (x *Query) GetAggregatorOperation() *Operation {
	if x != nil {
		return x.AggregatorOperation
	}
	return nil
}

type QueryOneTrace struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	QueryId string `protobuf:"bytes,1,opt,name=query_id,json=queryId,proto3" json:"query_id,omitempty"`
	TraceId string `protobuf:"bytes,2,opt,name=trace_id,json=traceId,proto3" json:"trace_id,omitempty"`
}

func (x *QueryOneTrace) Reset() {
	*x = QueryOneTrace{}
	if protoimpl.UnsafeEnabled {
		mi := &file_wirequery_proto_msgTypes[3]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *QueryOneTrace) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*QueryOneTrace) ProtoMessage() {}

func (x *QueryOneTrace) ProtoReflect() protoreflect.Message {
	mi := &file_wirequery_proto_msgTypes[3]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use QueryOneTrace.ProtoReflect.Descriptor instead.
func (*QueryOneTrace) Descriptor() ([]byte, []int) {
	return file_wirequery_proto_rawDescGZIP(), []int{3}
}

func (x *QueryOneTrace) GetQueryId() string {
	if x != nil {
		return x.QueryId
	}
	return ""
}

func (x *QueryOneTrace) GetTraceId() string {
	if x != nil {
		return x.TraceId
	}
	return ""
}

type QueryHead struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	AppName    string `protobuf:"bytes,1,opt,name=app_name,json=appName,proto3" json:"app_name,omitempty"`
	Method     string `protobuf:"bytes,2,opt,name=method,proto3" json:"method,omitempty"`
	Path       string `protobuf:"bytes,3,opt,name=path,proto3" json:"path,omitempty"`
	StatusCode string `protobuf:"bytes,4,opt,name=status_code,json=statusCode,proto3" json:"status_code,omitempty"`
}

func (x *QueryHead) Reset() {
	*x = QueryHead{}
	if protoimpl.UnsafeEnabled {
		mi := &file_wirequery_proto_msgTypes[4]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *QueryHead) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*QueryHead) ProtoMessage() {}

func (x *QueryHead) ProtoReflect() protoreflect.Message {
	mi := &file_wirequery_proto_msgTypes[4]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use QueryHead.ProtoReflect.Descriptor instead.
func (*QueryHead) Descriptor() ([]byte, []int) {
	return file_wirequery_proto_rawDescGZIP(), []int{4}
}

func (x *QueryHead) GetAppName() string {
	if x != nil {
		return x.AppName
	}
	return ""
}

func (x *QueryHead) GetMethod() string {
	if x != nil {
		return x.Method
	}
	return ""
}

func (x *QueryHead) GetPath() string {
	if x != nil {
		return x.Path
	}
	return ""
}

func (x *QueryHead) GetStatusCode() string {
	if x != nil {
		return x.StatusCode
	}
	return ""
}

type Operation struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	Name          string `protobuf:"bytes,1,opt,name=name,proto3" json:"name,omitempty"`
	CelExpression string `protobuf:"bytes,2,opt,name=cel_expression,json=celExpression,proto3" json:"cel_expression,omitempty"`
}

func (x *Operation) Reset() {
	*x = Operation{}
	if protoimpl.UnsafeEnabled {
		mi := &file_wirequery_proto_msgTypes[5]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *Operation) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*Operation) ProtoMessage() {}

func (x *Operation) ProtoReflect() protoreflect.Message {
	mi := &file_wirequery_proto_msgTypes[5]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use Operation.ProtoReflect.Descriptor instead.
func (*Operation) Descriptor() ([]byte, []int) {
	return file_wirequery_proto_rawDescGZIP(), []int{5}
}

func (x *Operation) GetName() string {
	if x != nil {
		return x.Name
	}
	return ""
}

func (x *Operation) GetCelExpression() string {
	if x != nil {
		return x.CelExpression
	}
	return ""
}

type QueryReports struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	QueryReports []*QueryReport `protobuf:"bytes,1,rep,name=query_reports,json=queryReports,proto3" json:"query_reports,omitempty"`
	ApiKey       string         `protobuf:"bytes,2,opt,name=api_key,json=apiKey,proto3" json:"api_key,omitempty"`
	AppName      string         `protobuf:"bytes,3,opt,name=app_name,json=appName,proto3" json:"app_name,omitempty"`
}

func (x *QueryReports) Reset() {
	*x = QueryReports{}
	if protoimpl.UnsafeEnabled {
		mi := &file_wirequery_proto_msgTypes[6]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *QueryReports) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*QueryReports) ProtoMessage() {}

func (x *QueryReports) ProtoReflect() protoreflect.Message {
	mi := &file_wirequery_proto_msgTypes[6]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use QueryReports.ProtoReflect.Descriptor instead.
func (*QueryReports) Descriptor() ([]byte, []int) {
	return file_wirequery_proto_rawDescGZIP(), []int{6}
}

func (x *QueryReports) GetQueryReports() []*QueryReport {
	if x != nil {
		return x.QueryReports
	}
	return nil
}

func (x *QueryReports) GetApiKey() string {
	if x != nil {
		return x.ApiKey
	}
	return ""
}

func (x *QueryReports) GetAppName() string {
	if x != nil {
		return x.AppName
	}
	return ""
}

type QueryReport struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	QueryId   string `protobuf:"bytes,1,opt,name=query_id,json=queryId,proto3" json:"query_id,omitempty"`
	Message   string `protobuf:"bytes,2,opt,name=message,proto3" json:"message,omitempty"`
	StartTime int64  `protobuf:"varint,3,opt,name=start_time,json=startTime,proto3" json:"start_time,omitempty"`
	EndTime   int64  `protobuf:"varint,4,opt,name=end_time,json=endTime,proto3" json:"end_time,omitempty"`
	TraceId   string `protobuf:"bytes,5,opt,name=trace_id,json=traceId,proto3" json:"trace_id,omitempty"`
}

func (x *QueryReport) Reset() {
	*x = QueryReport{}
	if protoimpl.UnsafeEnabled {
		mi := &file_wirequery_proto_msgTypes[7]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *QueryReport) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*QueryReport) ProtoMessage() {}

func (x *QueryReport) ProtoReflect() protoreflect.Message {
	mi := &file_wirequery_proto_msgTypes[7]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use QueryReport.ProtoReflect.Descriptor instead.
func (*QueryReport) Descriptor() ([]byte, []int) {
	return file_wirequery_proto_rawDescGZIP(), []int{7}
}

func (x *QueryReport) GetQueryId() string {
	if x != nil {
		return x.QueryId
	}
	return ""
}

func (x *QueryReport) GetMessage() string {
	if x != nil {
		return x.Message
	}
	return ""
}

func (x *QueryReport) GetStartTime() int64 {
	if x != nil {
		return x.StartTime
	}
	return 0
}

func (x *QueryReport) GetEndTime() int64 {
	if x != nil {
		return x.EndTime
	}
	return 0
}

func (x *QueryReport) GetTraceId() string {
	if x != nil {
		return x.TraceId
	}
	return ""
}

type Empty struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields
}

func (x *Empty) Reset() {
	*x = Empty{}
	if protoimpl.UnsafeEnabled {
		mi := &file_wirequery_proto_msgTypes[8]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *Empty) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*Empty) ProtoMessage() {}

func (x *Empty) ProtoReflect() protoreflect.Message {
	mi := &file_wirequery_proto_msgTypes[8]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use Empty.ProtoReflect.Descriptor instead.
func (*Empty) Descriptor() ([]byte, []int) {
	return file_wirequery_proto_rawDescGZIP(), []int{8}
}

var File_wirequery_proto protoreflect.FileDescriptor

var file_wirequery_proto_rawDesc = []byte{
	0x0a, 0x0f, 0x77, 0x69, 0x72, 0x65, 0x71, 0x75, 0x65, 0x72, 0x79, 0x2e, 0x70, 0x72, 0x6f, 0x74,
	0x6f, 0x12, 0x0b, 0x77, 0x69, 0x72, 0x65, 0x71, 0x75, 0x65, 0x72, 0x79, 0x70, 0x62, 0x22, 0x4d,
	0x0a, 0x17, 0x4c, 0x69, 0x73, 0x74, 0x65, 0x6e, 0x46, 0x6f, 0x72, 0x51, 0x75, 0x65, 0x72, 0x69,
	0x65, 0x73, 0x52, 0x65, 0x71, 0x75, 0x65, 0x73, 0x74, 0x12, 0x19, 0x0a, 0x08, 0x61, 0x70, 0x70,
	0x5f, 0x6e, 0x61, 0x6d, 0x65, 0x18, 0x01, 0x20, 0x01, 0x28, 0x09, 0x52, 0x07, 0x61, 0x70, 0x70,
	0x4e, 0x61, 0x6d, 0x65, 0x12, 0x17, 0x0a, 0x07, 0x61, 0x70, 0x69, 0x5f, 0x6b, 0x65, 0x79, 0x18,
	0x02, 0x20, 0x01, 0x28, 0x09, 0x52, 0x06, 0x61, 0x70, 0x69, 0x4b, 0x65, 0x79, 0x22, 0xce, 0x01,
	0x0a, 0x0d, 0x51, 0x75, 0x65, 0x72, 0x79, 0x4d, 0x75, 0x74, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x12,
	0x31, 0x0a, 0x09, 0x61, 0x64, 0x64, 0x5f, 0x71, 0x75, 0x65, 0x72, 0x79, 0x18, 0x01, 0x20, 0x01,
	0x28, 0x0b, 0x32, 0x12, 0x2e, 0x77, 0x69, 0x72, 0x65, 0x71, 0x75, 0x65, 0x72, 0x79, 0x70, 0x62,
	0x2e, 0x51, 0x75, 0x65, 0x72, 0x79, 0x48, 0x00, 0x52, 0x08, 0x61, 0x64, 0x64, 0x51, 0x75, 0x65,
	0x72, 0x79, 0x12, 0x2d, 0x0a, 0x12, 0x72, 0x65, 0x6d, 0x6f, 0x76, 0x65, 0x5f, 0x71, 0x75, 0x65,
	0x72, 0x79, 0x5f, 0x62, 0x79, 0x5f, 0x69, 0x64, 0x18, 0x02, 0x20, 0x01, 0x28, 0x09, 0x48, 0x00,
	0x52, 0x0f, 0x72, 0x65, 0x6d, 0x6f, 0x76, 0x65, 0x51, 0x75, 0x65, 0x72, 0x79, 0x42, 0x79, 0x49,
	0x64, 0x12, 0x44, 0x0a, 0x0f, 0x71, 0x75, 0x65, 0x72, 0x79, 0x5f, 0x6f, 0x6e, 0x65, 0x5f, 0x74,
	0x72, 0x61, 0x63, 0x65, 0x18, 0x03, 0x20, 0x01, 0x28, 0x0b, 0x32, 0x1a, 0x2e, 0x77, 0x69, 0x72,
	0x65, 0x71, 0x75, 0x65, 0x72, 0x79, 0x70, 0x62, 0x2e, 0x51, 0x75, 0x65, 0x72, 0x79, 0x4f, 0x6e,
	0x65, 0x54, 0x72, 0x61, 0x63, 0x65, 0x48, 0x00, 0x52, 0x0d, 0x71, 0x75, 0x65, 0x72, 0x79, 0x4f,
	0x6e, 0x65, 0x54, 0x72, 0x61, 0x63, 0x65, 0x42, 0x15, 0x0a, 0x13, 0x71, 0x75, 0x65, 0x72, 0x79,
	0x4d, 0x75, 0x74, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x43, 0x68, 0x6f, 0x69, 0x63, 0x65, 0x22, 0xe8,
	0x01, 0x0a, 0x05, 0x51, 0x75, 0x65, 0x72, 0x79, 0x12, 0x19, 0x0a, 0x08, 0x71, 0x75, 0x65, 0x72,
	0x79, 0x5f, 0x69, 0x64, 0x18, 0x01, 0x20, 0x01, 0x28, 0x09, 0x52, 0x07, 0x71, 0x75, 0x65, 0x72,
	0x79, 0x49, 0x64, 0x12, 0x34, 0x0a, 0x09, 0x71, 0x75, 0x65, 0x72, 0x79, 0x48, 0x65, 0x61, 0x64,
	0x18, 0x02, 0x20, 0x01, 0x28, 0x0b, 0x32, 0x16, 0x2e, 0x77, 0x69, 0x72, 0x65, 0x71, 0x75, 0x65,
	0x72, 0x79, 0x70, 0x62, 0x2e, 0x51, 0x75, 0x65, 0x72, 0x79, 0x48, 0x65, 0x61, 0x64, 0x52, 0x09,
	0x71, 0x75, 0x65, 0x72, 0x79, 0x48, 0x65, 0x61, 0x64, 0x12, 0x43, 0x0a, 0x11, 0x73, 0x74, 0x72,
	0x65, 0x61, 0x6d, 0x5f, 0x6f, 0x70, 0x65, 0x72, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x73, 0x18, 0x03,
	0x20, 0x03, 0x28, 0x0b, 0x32, 0x16, 0x2e, 0x77, 0x69, 0x72, 0x65, 0x71, 0x75, 0x65, 0x72, 0x79,
	0x70, 0x62, 0x2e, 0x4f, 0x70, 0x65, 0x72, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x52, 0x10, 0x73, 0x74,
	0x72, 0x65, 0x61, 0x6d, 0x4f, 0x70, 0x65, 0x72, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x73, 0x12, 0x49,
	0x0a, 0x14, 0x61, 0x67, 0x67, 0x72, 0x65, 0x67, 0x61, 0x74, 0x6f, 0x72, 0x5f, 0x6f, 0x70, 0x65,
	0x72, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x18, 0x04, 0x20, 0x01, 0x28, 0x0b, 0x32, 0x16, 0x2e, 0x77,
	0x69, 0x72, 0x65, 0x71, 0x75, 0x65, 0x72, 0x79, 0x70, 0x62, 0x2e, 0x4f, 0x70, 0x65, 0x72, 0x61,
	0x74, 0x69, 0x6f, 0x6e, 0x52, 0x13, 0x61, 0x67, 0x67, 0x72, 0x65, 0x67, 0x61, 0x74, 0x6f, 0x72,
	0x4f, 0x70, 0x65, 0x72, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x22, 0x45, 0x0a, 0x0d, 0x51, 0x75, 0x65,
	0x72, 0x79, 0x4f, 0x6e, 0x65, 0x54, 0x72, 0x61, 0x63, 0x65, 0x12, 0x19, 0x0a, 0x08, 0x71, 0x75,
	0x65, 0x72, 0x79, 0x5f, 0x69, 0x64, 0x18, 0x01, 0x20, 0x01, 0x28, 0x09, 0x52, 0x07, 0x71, 0x75,
	0x65, 0x72, 0x79, 0x49, 0x64, 0x12, 0x19, 0x0a, 0x08, 0x74, 0x72, 0x61, 0x63, 0x65, 0x5f, 0x69,
	0x64, 0x18, 0x02, 0x20, 0x01, 0x28, 0x09, 0x52, 0x07, 0x74, 0x72, 0x61, 0x63, 0x65, 0x49, 0x64,
	0x22, 0x73, 0x0a, 0x09, 0x51, 0x75, 0x65, 0x72, 0x79, 0x48, 0x65, 0x61, 0x64, 0x12, 0x19, 0x0a,
	0x08, 0x61, 0x70, 0x70, 0x5f, 0x6e, 0x61, 0x6d, 0x65, 0x18, 0x01, 0x20, 0x01, 0x28, 0x09, 0x52,
	0x07, 0x61, 0x70, 0x70, 0x4e, 0x61, 0x6d, 0x65, 0x12, 0x16, 0x0a, 0x06, 0x6d, 0x65, 0x74, 0x68,
	0x6f, 0x64, 0x18, 0x02, 0x20, 0x01, 0x28, 0x09, 0x52, 0x06, 0x6d, 0x65, 0x74, 0x68, 0x6f, 0x64,
	0x12, 0x12, 0x0a, 0x04, 0x70, 0x61, 0x74, 0x68, 0x18, 0x03, 0x20, 0x01, 0x28, 0x09, 0x52, 0x04,
	0x70, 0x61, 0x74, 0x68, 0x12, 0x1f, 0x0a, 0x0b, 0x73, 0x74, 0x61, 0x74, 0x75, 0x73, 0x5f, 0x63,
	0x6f, 0x64, 0x65, 0x18, 0x04, 0x20, 0x01, 0x28, 0x09, 0x52, 0x0a, 0x73, 0x74, 0x61, 0x74, 0x75,
	0x73, 0x43, 0x6f, 0x64, 0x65, 0x22, 0x46, 0x0a, 0x09, 0x4f, 0x70, 0x65, 0x72, 0x61, 0x74, 0x69,
	0x6f, 0x6e, 0x12, 0x12, 0x0a, 0x04, 0x6e, 0x61, 0x6d, 0x65, 0x18, 0x01, 0x20, 0x01, 0x28, 0x09,
	0x52, 0x04, 0x6e, 0x61, 0x6d, 0x65, 0x12, 0x25, 0x0a, 0x0e, 0x63, 0x65, 0x6c, 0x5f, 0x65, 0x78,
	0x70, 0x72, 0x65, 0x73, 0x73, 0x69, 0x6f, 0x6e, 0x18, 0x02, 0x20, 0x01, 0x28, 0x09, 0x52, 0x0d,
	0x63, 0x65, 0x6c, 0x45, 0x78, 0x70, 0x72, 0x65, 0x73, 0x73, 0x69, 0x6f, 0x6e, 0x22, 0x81, 0x01,
	0x0a, 0x0c, 0x51, 0x75, 0x65, 0x72, 0x79, 0x52, 0x65, 0x70, 0x6f, 0x72, 0x74, 0x73, 0x12, 0x3d,
	0x0a, 0x0d, 0x71, 0x75, 0x65, 0x72, 0x79, 0x5f, 0x72, 0x65, 0x70, 0x6f, 0x72, 0x74, 0x73, 0x18,
	0x01, 0x20, 0x03, 0x28, 0x0b, 0x32, 0x18, 0x2e, 0x77, 0x69, 0x72, 0x65, 0x71, 0x75, 0x65, 0x72,
	0x79, 0x70, 0x62, 0x2e, 0x51, 0x75, 0x65, 0x72, 0x79, 0x52, 0x65, 0x70, 0x6f, 0x72, 0x74, 0x52,
	0x0c, 0x71, 0x75, 0x65, 0x72, 0x79, 0x52, 0x65, 0x70, 0x6f, 0x72, 0x74, 0x73, 0x12, 0x17, 0x0a,
	0x07, 0x61, 0x70, 0x69, 0x5f, 0x6b, 0x65, 0x79, 0x18, 0x02, 0x20, 0x01, 0x28, 0x09, 0x52, 0x06,
	0x61, 0x70, 0x69, 0x4b, 0x65, 0x79, 0x12, 0x19, 0x0a, 0x08, 0x61, 0x70, 0x70, 0x5f, 0x6e, 0x61,
	0x6d, 0x65, 0x18, 0x03, 0x20, 0x01, 0x28, 0x09, 0x52, 0x07, 0x61, 0x70, 0x70, 0x4e, 0x61, 0x6d,
	0x65, 0x22, 0x97, 0x01, 0x0a, 0x0b, 0x51, 0x75, 0x65, 0x72, 0x79, 0x52, 0x65, 0x70, 0x6f, 0x72,
	0x74, 0x12, 0x19, 0x0a, 0x08, 0x71, 0x75, 0x65, 0x72, 0x79, 0x5f, 0x69, 0x64, 0x18, 0x01, 0x20,
	0x01, 0x28, 0x09, 0x52, 0x07, 0x71, 0x75, 0x65, 0x72, 0x79, 0x49, 0x64, 0x12, 0x18, 0x0a, 0x07,
	0x6d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x18, 0x02, 0x20, 0x01, 0x28, 0x09, 0x52, 0x07, 0x6d,
	0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x12, 0x1d, 0x0a, 0x0a, 0x73, 0x74, 0x61, 0x72, 0x74, 0x5f,
	0x74, 0x69, 0x6d, 0x65, 0x18, 0x03, 0x20, 0x01, 0x28, 0x03, 0x52, 0x09, 0x73, 0x74, 0x61, 0x72,
	0x74, 0x54, 0x69, 0x6d, 0x65, 0x12, 0x19, 0x0a, 0x08, 0x65, 0x6e, 0x64, 0x5f, 0x74, 0x69, 0x6d,
	0x65, 0x18, 0x04, 0x20, 0x01, 0x28, 0x03, 0x52, 0x07, 0x65, 0x6e, 0x64, 0x54, 0x69, 0x6d, 0x65,
	0x12, 0x19, 0x0a, 0x08, 0x74, 0x72, 0x61, 0x63, 0x65, 0x5f, 0x69, 0x64, 0x18, 0x05, 0x20, 0x01,
	0x28, 0x09, 0x52, 0x07, 0x74, 0x72, 0x61, 0x63, 0x65, 0x49, 0x64, 0x22, 0x07, 0x0a, 0x05, 0x45,
	0x6d, 0x70, 0x74, 0x79, 0x32, 0xb3, 0x01, 0x0a, 0x10, 0x57, 0x69, 0x72, 0x65, 0x71, 0x75, 0x65,
	0x72, 0x79, 0x53, 0x65, 0x72, 0x76, 0x69, 0x63, 0x65, 0x12, 0x58, 0x0a, 0x10, 0x4c, 0x69, 0x73,
	0x74, 0x65, 0x6e, 0x46, 0x6f, 0x72, 0x51, 0x75, 0x65, 0x72, 0x69, 0x65, 0x73, 0x12, 0x24, 0x2e,
	0x77, 0x69, 0x72, 0x65, 0x71, 0x75, 0x65, 0x72, 0x79, 0x70, 0x62, 0x2e, 0x4c, 0x69, 0x73, 0x74,
	0x65, 0x6e, 0x46, 0x6f, 0x72, 0x51, 0x75, 0x65, 0x72, 0x69, 0x65, 0x73, 0x52, 0x65, 0x71, 0x75,
	0x65, 0x73, 0x74, 0x1a, 0x1a, 0x2e, 0x77, 0x69, 0x72, 0x65, 0x71, 0x75, 0x65, 0x72, 0x79, 0x70,
	0x62, 0x2e, 0x51, 0x75, 0x65, 0x72, 0x79, 0x4d, 0x75, 0x74, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x22,
	0x00, 0x30, 0x01, 0x12, 0x45, 0x0a, 0x12, 0x52, 0x65, 0x70, 0x6f, 0x72, 0x74, 0x51, 0x75, 0x65,
	0x72, 0x79, 0x52, 0x65, 0x73, 0x75, 0x6c, 0x74, 0x73, 0x12, 0x19, 0x2e, 0x77, 0x69, 0x72, 0x65,
	0x71, 0x75, 0x65, 0x72, 0x79, 0x70, 0x62, 0x2e, 0x51, 0x75, 0x65, 0x72, 0x79, 0x52, 0x65, 0x70,
	0x6f, 0x72, 0x74, 0x73, 0x1a, 0x12, 0x2e, 0x77, 0x69, 0x72, 0x65, 0x71, 0x75, 0x65, 0x72, 0x79,
	0x70, 0x62, 0x2e, 0x45, 0x6d, 0x70, 0x74, 0x79, 0x22, 0x00, 0x42, 0x0f, 0x5a, 0x0d, 0x2e, 0x2f,
	0x77, 0x69, 0x72, 0x65, 0x71, 0x75, 0x65, 0x72, 0x79, 0x70, 0x62, 0x62, 0x06, 0x70, 0x72, 0x6f,
	0x74, 0x6f, 0x33,
}

var (
	file_wirequery_proto_rawDescOnce sync.Once
	file_wirequery_proto_rawDescData = file_wirequery_proto_rawDesc
)

func file_wirequery_proto_rawDescGZIP() []byte {
	file_wirequery_proto_rawDescOnce.Do(func() {
		file_wirequery_proto_rawDescData = protoimpl.X.CompressGZIP(file_wirequery_proto_rawDescData)
	})
	return file_wirequery_proto_rawDescData
}

var file_wirequery_proto_msgTypes = make([]protoimpl.MessageInfo, 9)
var file_wirequery_proto_goTypes = []interface{}{
	(*ListenForQueriesRequest)(nil), // 0: wirequerypb.ListenForQueriesRequest
	(*QueryMutation)(nil),           // 1: wirequerypb.QueryMutation
	(*Query)(nil),                   // 2: wirequerypb.Query
	(*QueryOneTrace)(nil),           // 3: wirequerypb.QueryOneTrace
	(*QueryHead)(nil),               // 4: wirequerypb.QueryHead
	(*Operation)(nil),               // 5: wirequerypb.Operation
	(*QueryReports)(nil),            // 6: wirequerypb.QueryReports
	(*QueryReport)(nil),             // 7: wirequerypb.QueryReport
	(*Empty)(nil),                   // 8: wirequerypb.Empty
}
var file_wirequery_proto_depIdxs = []int32{
	2, // 0: wirequerypb.QueryMutation.add_query:type_name -> wirequerypb.Query
	3, // 1: wirequerypb.QueryMutation.query_one_trace:type_name -> wirequerypb.QueryOneTrace
	4, // 2: wirequerypb.Query.queryHead:type_name -> wirequerypb.QueryHead
	5, // 3: wirequerypb.Query.stream_operations:type_name -> wirequerypb.Operation
	5, // 4: wirequerypb.Query.aggregator_operation:type_name -> wirequerypb.Operation
	7, // 5: wirequerypb.QueryReports.query_reports:type_name -> wirequerypb.QueryReport
	0, // 6: wirequerypb.WirequeryService.ListenForQueries:input_type -> wirequerypb.ListenForQueriesRequest
	6, // 7: wirequerypb.WirequeryService.ReportQueryResults:input_type -> wirequerypb.QueryReports
	1, // 8: wirequerypb.WirequeryService.ListenForQueries:output_type -> wirequerypb.QueryMutation
	8, // 9: wirequerypb.WirequeryService.ReportQueryResults:output_type -> wirequerypb.Empty
	8, // [8:10] is the sub-list for method output_type
	6, // [6:8] is the sub-list for method input_type
	6, // [6:6] is the sub-list for extension type_name
	6, // [6:6] is the sub-list for extension extendee
	0, // [0:6] is the sub-list for field type_name
}

func init() { file_wirequery_proto_init() }
func file_wirequery_proto_init() {
	if File_wirequery_proto != nil {
		return
	}
	if !protoimpl.UnsafeEnabled {
		file_wirequery_proto_msgTypes[0].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*ListenForQueriesRequest); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_wirequery_proto_msgTypes[1].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*QueryMutation); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_wirequery_proto_msgTypes[2].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*Query); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_wirequery_proto_msgTypes[3].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*QueryOneTrace); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_wirequery_proto_msgTypes[4].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*QueryHead); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_wirequery_proto_msgTypes[5].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*Operation); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_wirequery_proto_msgTypes[6].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*QueryReports); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_wirequery_proto_msgTypes[7].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*QueryReport); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_wirequery_proto_msgTypes[8].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*Empty); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
	}
	file_wirequery_proto_msgTypes[1].OneofWrappers = []interface{}{
		(*QueryMutation_AddQuery)(nil),
		(*QueryMutation_RemoveQueryById)(nil),
		(*QueryMutation_QueryOneTrace)(nil),
	}
	type x struct{}
	out := protoimpl.TypeBuilder{
		File: protoimpl.DescBuilder{
			GoPackagePath: reflect.TypeOf(x{}).PkgPath(),
			RawDescriptor: file_wirequery_proto_rawDesc,
			NumEnums:      0,
			NumMessages:   9,
			NumExtensions: 0,
			NumServices:   1,
		},
		GoTypes:           file_wirequery_proto_goTypes,
		DependencyIndexes: file_wirequery_proto_depIdxs,
		MessageInfos:      file_wirequery_proto_msgTypes,
	}.Build()
	File_wirequery_proto = out.File
	file_wirequery_proto_rawDesc = nil
	file_wirequery_proto_goTypes = nil
	file_wirequery_proto_depIdxs = nil
}
