// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

const {
  PHASE_DEVELOPMENT_SERVER,
  PHASE_PRODUCTION_BUILD,
} = require('next/constants')

const getBuildConfig = () => {
  const path = require('path')
  const postcssPresetEnv = require('postcss-preset-env')
  const postcssPresetEnvOptions = {
    features: {
      'custom-media-queries': true,
      'custom-selectors': true,
    },
  }

  const cssOptions = {
    postcssLoaderOptions: {
      plugins: [postcssPresetEnv(postcssPresetEnvOptions)],
    },
    sassOptions: {
      includePaths: [path.join(process.cwd(), 'src', 'common', 'css')],
    },
  }

  const nextConfig = {
    ...cssOptions,
    webpack(config) {
      config.module.rules.push({
        test: /\.svg$/,
        include: path.join(process.cwd(), 'src', 'components', 'icon', 'icons'),
        use: [
          'svg-sprite-loader',
          {
            loader: 'svgo-loader',
            options: {
              plugins: [
                {removeAttrs: {attrs: '(fill)'}},
                {removeTitle: true},
                {cleanupIDs: true},
                {removeStyleElement: true},
              ],
            },
          },
        ],
      })
      return config
    },
  }
  return nextConfig
}

const withImages = require("next-images");

module.exports = {
  async rewrites() {
    return [
      {
        source: '/graphql',
        destination: 'http://localhost:8080/graphql',
      },
      {
        source: '/subscriptions',
        destination: 'http://localhost:8080/subscriptions',
      },
    ]
  },
  output: 'standalone',
  ...withImages((phase) => {
    const shouldAddBuildConfig =
      phase === PHASE_DEVELOPMENT_SERVER || phase === PHASE_PRODUCTION_BUILD
    return shouldAddBuildConfig ? getBuildConfig() : {}
  })
}


