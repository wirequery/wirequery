// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  async rewrites() {
    return [
      {
        source: '/products',
        destination: 'http://localhost:9101/products'
      },
      {
        source: '/products/:path*',
        destination: 'http://localhost:9101/products/:path*'
      },
      {
        source: '/basket-entries',
        destination: 'http://localhost:9100/basket-entries'
      },
      {
        source: '/basket-entries/:path*',
        destination: 'http://localhost:9100/basket-entries/:path*'
      }
    ]
  }
}

module.exports = nextConfig
