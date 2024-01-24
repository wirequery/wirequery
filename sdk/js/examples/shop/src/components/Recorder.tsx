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
const wireQueryBackendPath = 'http://localhost:8080';

export const Recorder = () => {
  const [isRecordingModalOpen, setIsRecordingModalOpen] = useState(false);
  const { mutate } = useSWRConfig()

  const clearCache = () => mutate(() => true, undefined, { revalidate: true })

  const [recording, setRecording] = useState<any | undefined>(undefined);

  const start = () => {
    setIsRecordingModalOpen(true)
    const destructor = startRecording();
    return () => destructor?.();
  };

  const startRecording = () => {
    fetch("http://localhost:8080/api/v1/recordings", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        templateId: 1, // Debug Session by Account Id
        args: {
          accountId: "123",
        },
      }),
    }).then((res) => res.json().then((json) => {
      setRecording(json);

      // Refresh all calls once, so that they will be intercepted by WireQuery.
      setTimeout(() => {
        clearCache()
        setIsRecordingModalOpen(false)
      }, 1000)
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
