package masking

import (
	"reflect"
	"testing"
)

func TestMask(t *testing.T) {
	var (
		mask   = "MASK"
		unmask = "UNMASK"
	)

	type args struct {
		input    interface{}
		settings MaskSettings
	}
	tests := []struct {
		name string
		args args
		want interface{}
	}{
		{
			name: "without settings, the input is masked",
			args: args{
				input: "field",
				settings: MaskSettings{
					Fields: nil,
				},
			},
			want: "masked",
		}, {
			name: "if the root is unmasked, the input is unmasked",
			args: args{
				input: "field",
				settings: MaskSettings{
					Type:   &unmask,
					Fields: nil,
				},
			},
			want: "field",
		}, {
			name: "if root is masked, the input is masked",
			args: args{
				input: "field",
				settings: MaskSettings{
					Type: &mask,
				},
			},
			want: "masked",
		}, {
			name: "without settings, all fields in object are masked",
			args: args{
				input:    map[string]any{"name": "wouter"},
				settings: MaskSettings{},
			},
			want: map[string]any{"name": "masked"},
		},
		{
			name: "if root is masked, all fields in object are masked",
			args: args{
				input: map[string]any{"name": "wouter"},
				settings: MaskSettings{
					Type: &mask,
				},
			},
			want: map[string]any{"name": "masked"},
		},
		{
			name: "if root is unmasked, all fields in object are masked",
			args: args{
				input: map[string]any{"name": "wouter"},
				settings: MaskSettings{
					Type: &unmask,
				},
			},
			want: map[string]any{"name": "wouter"},
		},
		{
			name: "if root is masked, but a subfield is unmasked, it will unmask that subfield",
			args: args{
				input: map[string]string{"name": "wouter"},
				settings: MaskSettings{
					Type: &mask,
					Fields: &map[string]MaskSettings{"name": {
						Type: &unmask,
					}},
				},
			},
			want: map[string]any{"name": "wouter"},
		},
		{
			name: "if root is unmasked, but a subfield is masked, it will mask that subfield",
			args: args{
				input: map[string]string{"name": "wouter"},
				settings: MaskSettings{
					Type: &unmask,
					Fields: &map[string]MaskSettings{"name": {
						Type: &mask,
					}},
				},
			},
			want: map[string]any{"name": "masked"},
		},
		{
			name: "if a slice is unmasked, it will not be masked",
			args: args{
				input: []any{"wouter"},
				settings: MaskSettings{
					Type: &unmask,
				},
			},
			want: []any{"wouter"},
		},
		{
			name: "if a slice is masked, it will be masked",
			args: args{
				input: []any{"wouter"},
				settings: MaskSettings{
					Type: &mask,
				},
			},
			want: []any{"masked"},
		},
		{
			name: "if an slice is unmasked, the sub objects are not masked",
			args: args{
				input: []any{map[string]any{"a": "wouter"}},
				settings: MaskSettings{
					Type: &unmask,
				},
			},
			want: []any{map[string]any{"a": "wouter"}},
		},
		{
			name: "if a slice is masked, the sub objects are masked",
			args: args{
				input: []any{map[string]any{"a": "wouter"}},
				settings: MaskSettings{
					Type: &mask,
				},
			},
			want: []any{map[string]any{"a": "masked"}},
		},
		{
			name: "if an array is unmasked, the sub objects are not masked",
			args: args{
				input: [1]any{1},
				settings: MaskSettings{
					Type: &mask,
				},
			},
			want: []any{"masked"},
		},
		{
			name: "if an array is masked, the sub objects are masked",
			args: args{
				input: [1]any{1},
				settings: MaskSettings{
					Type: &unmask,
				},
			},
			want: []any{1},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := Mask(tt.args.input, tt.args.settings); !reflect.DeepEqual(got, tt.want) {
				t.Errorf("Mask() = %v, want %v", got, tt.want)
			}
		})
	}
}
