// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

import { getRecordConsolePlugin, record } from "rrweb"
import { getRecordNetworkPlugin } from './recordNetworkPlugin'
import type { eventWithTime } from '@rrweb/types';

interface Recording {
  id: number,
  secret: string,
  correlationId: string,
}

export class Recorder {
  private recording?: Recording
  private events: eventWithTime[] = []

  constructor(
    private wirequeryUrl: string,
    private templateId: number,
    private apiKey: string) {
  }

  get correlationId(): string | undefined {
      return this.recording?.correlationId
  }

  startRecording(args: object) {
    if (this.recording) {
      return Promise.reject('Recording already started.')
    }
    return fetch(`${this.wirequeryUrl}/api/v1/recordings`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        templateId: this.templateId,
        apiKey: this.apiKey,
        args,
      }),
    }).then(data => {
      data.json().then(json => {
        this.recording = json;
        (window as any).recordingCorrelationId = json.correlationId
      })
    }).then(() => {
      return record({
        emit: event => {
          this.events.push(event);
        },
        plugins: [getRecordConsolePlugin(), getRecordNetworkPlugin({ recordHeaders: false })],
      });
    })
  }
  
  stopRecording() {
    if (!this.recording) {
      return Promise.reject('No recording started.')
    }
    const recording = this.recording
    const events = this.events;

    (window as any).recordingCorrelationId = undefined
    this.recording = undefined
    this.events = []

    return fetch(`${this.wirequeryUrl}/api/v1/recordings/${recording.id}/finish`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        secret: recording.secret,
        recording: JSON.stringify(events),
        context: {},
      }),
    })
  }
}
