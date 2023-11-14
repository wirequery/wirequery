// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

import { Affix, Loader, Modal, rem } from "@mantine/core";
import { useState } from "react";
import { record, getRecordConsolePlugin } from "rrweb";
import { useSWRConfig } from "swr";

const events: any = [];

export const Recorder = () => {
  const [isRecordingModalOpen, setIsRecordingModalOpen] = useState(false);
  const { mutate } = useSWRConfig()

  const clearCache = () => mutate(() => true, undefined, { revalidate: true })

  const [recording, setRecording] = useState<any | undefined>(undefined);

  const start = () => {
    setIsRecordingModalOpen(true)
    console.log("effect");
    const destructor = startRecording();
    return () => {
      console.log("unmount");
      destructor?.();
    };
  };

  const startRecording = () => {
    fetch("https://demo.wirequery.io/api/v1/recordings", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        templateId: 3, // Debug Session by Account Id
        args: {
          accountId: "NL69FAKE8085990849",
        },
        lookbackSecs: 0,
        timeoutSecs: 30,
      }),
    }).then((res) => res.json().then((json) => {
      console.log(json)
      setRecording(json);
      setTimeout(() => {
        clearCache()
        setIsRecordingModalOpen(false)
      }, 1000) // Trigger all calls once more
    }));
    return record({
      emit: (event) => {
        events.push(event);
      },
      plugins: [getRecordConsolePlugin()],
    });
  };

  const send = () => {
    if (recording) {
      setRecording(undefined);
      fetch(`https://demo.wirequery.io/api/v1/recordings/${recording.id}/finish`, {
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
    <>
      <Affix position={{ bottom: 0, right: rem(20) }}>
        {recording
          ? <button onClick={send}>Done</button>
          : <button onClick={start}>Start Recording</button>}
      </Affix>
      <Modal
        title="Initializing Recording..."
        onClose={() => { }}
        opened={isRecordingModalOpen}
        closeButtonProps={{ display: 'none' }}
        closeOnClickOutside={false}
        closeOnEscape={false}
      >
        <Loader color="blue" />
      </Modal>
    </>
  );
};
