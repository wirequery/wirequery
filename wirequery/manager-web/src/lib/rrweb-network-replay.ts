// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import type { eventWithTime } from '@rrweb/types'
import { EventType } from '@rrweb/types'

export type InitiatorType =
  | 'audio'
  | 'beacon'
  | 'body'
  | 'css'
  | 'early-hint'
  | 'embed'
  | 'fetch'
  | 'frame'
  | 'iframe'
  | 'icon'
  | 'image'
  | 'img'
  | 'input'
  | 'link'
  | 'navigation'
  | 'object'
  | 'ping'
  | 'script'
  | 'track'
  | 'video'
  | 'xmlhttprequest'

type NetworkRequest = {
  url: string
  method?: string
  initiatorType: InitiatorType
  status?: number
  startTime: number
  endTime: number
  requestHeaders?: Headers
  requestBody?: Body
  responseHeaders?: Headers
  responseBody?: Body
}

type NetworkData = {
  requests: NetworkRequest[]
  isInitial?: boolean
}

type OnNetworkData = (data: NetworkData) => void

type NetworkReplayOptions = {
  onNetworkData: OnNetworkData
}

const NETWORK_PLUGIN_NAME = 'rrweb/network@1'

export const getReplayNetworkPlugin: (options: NetworkReplayOptions) => any = (
  options
) => {
  return {
    handler(event: eventWithTime) {
      if (
        event.type === EventType.Plugin &&
        event.data.plugin === NETWORK_PLUGIN_NAME
      ) {
        const networkData = event.data.payload as NetworkData
        options.onNetworkData(networkData)
      }
    },
  }
}
