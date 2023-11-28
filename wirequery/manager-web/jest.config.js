// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

const nextJest = require('next/jest')

const createJestConfig = nextJest({
  dir: './',
})

const customJestConfig = {
  coverageThreshold: {
    global: {
      statements: 80
    }
  },
  setupFilesAfterEnv: ['<rootDir>/jest.setup.js'],
  moduleDirectories: ['node_modules', '<rootDir>/'],
  testEnvironment: 'jest-environment-jsdom',
  moduleNameMapper: {
    "^\\@components/(.*)$": "<rootDir>/src/components/$1",
    "^d3$": "<rootDir>/node_modules/d3/dist/d3.min.js",
    "^\\@lib/(.*)$": "<rootDir>/src/lib/$1",
    "^\\@generated/(.*)$": "<rootDir>/src/generated/$1"
  }
}

module.exports = createJestConfig(customJestConfig)
