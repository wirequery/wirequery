// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

import { getRecordNetworkPlugin } from "@/replay/rrwebRecord";
import { Affix, rem } from "@mantine/core";
import { useState } from "react";
import { getRecordConsolePlugin, record } from "rrweb";

const events: any = [];
const wireQueryBackendPath = 'http://localhost:8080';

export const Recorder = () => {
  const [recording, setRecording] = useState<any | undefined>(undefined)

  const start = () => {
    const destructor = startRecording()
    return () => destructor?.();
  };

  const startRecording = () => {
    fetch(`${wireQueryBackendPath}/api/v1/recordings`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        templateId: 1, // Debug Session by Account Id
        apiKey: '1/f2e74d68-f70e-48bd-94dc-ce889f612d73',
        args: {
          accountId: "123",
        },
      }),
    }).then((res) => res.json().then((json) => {
      setRecording(json);
    }));
    return record({
      emit: (event) => {
        events.push(event);
      },
      plugins: [getRecordConsolePlugin(), getRecordNetworkPlugin({ recordHeaders: true })],
    });
  };

  const send = () => {
    if (recording) {
      setRecording(undefined);
      fetch(`${wireQueryBackendPath}/api/v1/recordings/${recording.id}/finish`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          secret: recording.secret,
          recording: JSON.stringify(events),
          context: {},
        }),
      });
    }
  };

  return (
    <Affix position={{ bottom: 0, right: rem(20) }}>
      {recording
        ? <button onClick={send}>Done</button>
        : <button onClick={start}>Start Recording</button>}
    </Affix>
  );
};
