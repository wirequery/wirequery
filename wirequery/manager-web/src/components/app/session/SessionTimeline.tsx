import React, { useEffect, useRef } from 'react'
import * as d3 from 'd3'
import { useMantineTheme } from '@mantine/core'

interface TimelineEvent {
  name: string
  events: { start: number; end: number }[]
}

interface TimelineProps {
  data: TimelineEvent[]
  currentTime?: number
  onEventClick: (event: TimelineEvent) => void
}

export const SessionTimeline: React.FC<TimelineProps> = ({
  currentTime,
  data,
  onEventClick,
}) => {
  const parentRef = useRef<HTMLDivElement | null>(null)
  const svgRef = useRef<SVGSVGElement | null>(null)

  const theme = useMantineTheme()

  useEffect(() => {
    // TODO too many objcts created ... etc.
    const parentDiv = parentRef.current

    const margin = { top: 20, right: 0, bottom: 30, left: 220 }
    const width = (parentDiv?.clientWidth ?? 0) - margin.left - margin.right
    const height = data.length * 50

    const res = d3
      .select(svgRef.current)
      .attr('width', width + margin.left + margin.right)
      .attr('height', height + margin.top + margin.bottom)

    res.selectAll('*').remove()

    const svg = res
      .append('g')
      .attr('transform', `translate(${margin.left},${margin.top})`)

    svg
      .append('rect')
      .attr('width', width)
      .attr('height', height)
      .attr(
        'fill',
        theme.colorScheme === 'dark'
          ? theme.colors.dark[8]
          : theme.colors.white ?? '#FFF'
      )

    const yScale = d3
      .scaleBand()
      .domain(data.map((event) => event.name))
      .range([0, height])
      .padding(0.1)

    const xScale = d3
      .scaleLinear()
      .domain([
        d3.min(data, (event: any) =>
          d3.min(event.events, (e: { start: number; end: number }) => e.start)
        ) || 0,
        d3.max(data, (event: any) =>
          d3.max(event.events, (e: { start: number; end: number }) => e.end)
        ) || 1,
      ])
      .range([0, width])

    svg
      .append('g')
      .selectAll('g')
      .data(data)
      .enter()
      .append('g')
      .attr(
        'transform',
        (event: any) =>
          `translate(0, ${
            (yScale(event.name) ?? 0) + yScale.bandwidth() / 4 + 2
          })`
      )
      .selectAll('rect')
      .data((event: any) => event.events)
      .enter()
      .append('rect')
      .attr('cursor', 'pointer')
      .attr('stroke', theme.colors.purple[5])
      .attr('stroke-width', '5px')
      .attr('x', (event: any) => xScale(event.start))
      .attr('width', (event: any) => event.end - event.start)
      .attr('height', yScale.bandwidth() / 2 - 4)
      .attr('fill', theme.colors['purple'][5])
      .on('mouseup', (clickEvent: any, event: any) => {
        event.onClick?.(
          xScale.invert(d3.pointer(clickEvent, svg.node() as SVGSVGElement)[0])
        )
      })

    const timeFormat = d3.timeFormat('%I:%M:%S')

    svg
      .append('rect')
      .attr('x', -margin.left)
      .attr('width', margin.left)
      .attr('height', height)
      .attr(
        'fill',
        theme.colorScheme === 'dark'
          ? theme.colors.dark[8]
          : theme.colors.white ?? '#FFF'
      )

    svg
      .append('g')
      .style('font', '13px arial')
      .attr(
        'color',
        theme.colorScheme === 'dark'
          ? theme.colors.gray[6]
          : theme.colors.gray[6]
      )
      .attr('transform', `translate(0, ${height})rotate(0)`)
      .call(
        d3
          .axisBottom(xScale)
          .ticks(4)
          .tickFormat(timeFormat as any)
      )
      .call((g) =>
        g
          .selectAll('.tick > text')
          .attr('fill', () =>
            theme.colorScheme === 'dark'
              ? theme.colors.gray[6]
              : theme.colors.gray[8]
          )
      )

    svg
      .append('g')
      .style('font', '13px arial')
      .attr(
        'color',
        theme.colorScheme === 'dark'
          ? theme.colors.gray[6]
          : theme.colors.gray[6]
      )
      .call(d3.axisLeft(yScale))
      .call((g) =>
        g
          .selectAll('.tick > text')
          .attr('fill', () =>
            theme.colorScheme === 'dark'
              ? theme.colors.gray[6]
              : theme.colors.gray[8]
          )
      )

    svg
      .append('line')
      .attr('class', 'vertical-line')
      .attr('x1', xScale(currentTime as any))
      .attr('x2', xScale(currentTime as any))
      .attr('y1', 0)
      .attr('y2', height)
      .style(
        'stroke',
        theme.colorScheme === 'dark'
          ? '' + (theme.colors.white ?? '#FFF')
          : theme.colors.gray[6]
      )
      .style('stroke-dasharray', '5,5')
  }, [data, onEventClick, currentTime])

  return (
    <div ref={parentRef}>
      <svg ref={svgRef}></svg>
    </div>
  )
}
