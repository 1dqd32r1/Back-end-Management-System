type Point = { label: string; value: number };

export function barChartSvg(
  points: Point[],
  options: { color: string; unit: string; height?: number },
): string {
  const h = options.height ?? 140;
  const padL = 36;
  const padR = 12;
  const padT = 12;
  const padB = 28;
  const n = points.length;
  const max = Math.max(...points.map((p) => p.value), 1);
  const innerW = 280 - padL - padR;
  const innerH = h - padT - padB;
  const barW = Math.max(8, (innerW / n) * 0.55);
  const gap = innerW / n - barW;

  const bars = points
    .map((p, i) => {
      const bh = (p.value / max) * innerH;
      const x = padL + i * (barW + gap) + gap / 2;
      const y = padT + innerH - bh;
      return `<rect x="${x.toFixed(1)}" y="${y.toFixed(1)}" width="${barW.toFixed(1)}" height="${bh.toFixed(1)}" rx="4" fill="${options.color}" opacity="0.9"/>`;
    })
    .join("");

  const labels = points
    .map((p, i) => {
      const x = padL + i * (barW + gap) + gap / 2 + barW / 2;
      return `<text x="${x.toFixed(1)}" y="${h - 8}" text-anchor="middle" class="chart-label">${escapeXml(p.label)}</text>`;
    })
    .join("");

  const axisY = `<line x1="${padL}" y1="${padT}" x2="${padL}" y2="${padT + innerH}" stroke="currentColor" stroke-opacity="0.2" stroke-width="1"/>`;
  const axisX = `<line x1="${padL}" y1="${padT + innerH}" x2="${280 - padR}" y2="${padT + innerH}" stroke="currentColor" stroke-opacity="0.2" stroke-width="1"/>`;

  return `<svg class="chart-svg" viewBox="0 0 280 ${h}" role="img" aria-label="趋势图">${axisY}${axisX}${bars}${labels}</svg><div class="chart-unit-hint">${options.unit}</div>`;
}

export function lineChartSvg(
  points: Point[],
  options: { color: string; unit: string; height?: number },
): string {
  const h = options.height ?? 140;
  const padL = 36;
  const padR = 12;
  const padT = 16;
  const padB = 28;
  const max = Math.max(...points.map((p) => p.value), 1);
  const innerW = 280 - padL - padR;
  const innerH = h - padT - padB;
  const n = points.length;
  const step = n > 1 ? innerW / (n - 1) : 0;

  const coords = points.map((p, i) => {
    const x = padL + i * step;
    const y = padT + innerH - (p.value / max) * innerH;
    return { x, y, label: p.label };
  });

  const d = coords
    .map((c, i) => `${i === 0 ? "M" : "L"} ${c.x.toFixed(1)} ${c.y.toFixed(1)}`)
    .join(" ");

  const areaD = `${d} L ${coords[coords.length - 1]!.x.toFixed(1)} ${(padT + innerH).toFixed(1)} L ${coords[0]!.x.toFixed(1)} ${(padT + innerH).toFixed(1)} Z`;

  const dots = coords
    .map(
      (c) =>
        `<circle cx="${c.x.toFixed(1)}" cy="${c.y.toFixed(1)}" r="4" fill="${options.color}" stroke="var(--van-background-2)" stroke-width="2"/>`,
    )
    .join("");

  const labels = coords
    .map(
      (c) =>
        `<text x="${c.x.toFixed(1)}" y="${h - 8}" text-anchor="middle" class="chart-label">${escapeXml(c.label)}</text>`,
    )
    .join("");

  const axisY = `<line x1="${padL}" y1="${padT}" x2="${padL}" y2="${padT + innerH}" stroke="currentColor" stroke-opacity="0.2" stroke-width="1"/>`;
  const axisX = `<line x1="${padL}" y1="${padT + innerH}" x2="${280 - padR}" y2="${padT + innerH}" stroke="currentColor" stroke-opacity="0.2" stroke-width="1"/>`;

  return `<svg class="chart-svg" viewBox="0 0 280 ${h}" role="img" aria-label="折线图">${axisY}${axisX}<path d="${areaD}" fill="${options.color}" fill-opacity="0.12"/><path d="${d}" fill="none" stroke="${options.color}" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>${dots}${labels}</svg><div class="chart-unit-hint">${options.unit}</div>`;
}

function escapeXml(s: string): string {
  return s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
}
