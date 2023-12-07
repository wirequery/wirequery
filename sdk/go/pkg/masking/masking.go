package masking

import (
	"reflect"
)

type MaskSettings struct {
	Type   *string
	Fields *map[string]MaskSettings
}

type JsonSettings struct {
	Settings MaskSettings
}

func Mask(input interface{}, settings MaskSettings) interface{} {
	return maskOrUnmask(input, settings, false)
}

func maskOrUnmask(input interface{}, settings MaskSettings, unmask bool) interface{} {
	if settings.Type != nil {
		if *settings.Type == "UNMASK" && !unmask {
			return maskOrUnmask(input, settings, true)
		} else if *settings.Type == "MASK" && unmask {
			return maskOrUnmask(input, settings, false)
		}
	}
	v := reflect.ValueOf(input)
	if v.Kind() == reflect.Map {
		var result = make(map[string]any)
		for _, key := range v.MapKeys() {
			if settings.Fields != nil {
				result[key.String()] = maskOrUnmask(v.MapIndex(key).Interface(), (*settings.Fields)[key.String()], unmask)
			} else {
				result[key.String()] = maskOrUnmask(v.MapIndex(key).Interface(), MaskSettings{}, unmask)
			}
		}
		return result
	} else if v.Kind() == reflect.Slice || v.Kind() == reflect.Array {
		var result []any
		for i := 0; i < v.Len(); i++ {
			result = append(result, maskOrUnmask(v.Index(i).Interface(), settings, unmask))
		}
		return result
	} else if unmask {
		return input
	} else {
		return "masked"
	}
}
