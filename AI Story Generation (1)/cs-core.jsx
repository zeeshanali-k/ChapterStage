/* ChapterStage — core: tokens, data, icons, primitives.
   Softer-KitOS dark system. Exposed on window for sibling babel scripts. */
const { useState, useEffect, useRef, useMemo } = React;

/* ── Design tokens ─────────────────────────────────────────── */
const T = {
  bg:        '#0A0E18',
  bg2:       '#0C111D',
  surface:   '#131A29',
  surfaceEl: '#19223499',
  surfaceHi: '#202B41',
  line:      'rgba(255,255,255,0.07)',
  lineHi:    'rgba(255,255,255,0.13)',
  primary:   '#7C5CFF',
  primaryDim:'rgba(124,92,255,0.16)',
  text:      '#F1F4FA',
  text2:     '#98A4BC',
  text3:     '#5C6982',
  success:   '#2EE59D',
  warn:      '#F6C85F',
  error:     '#FF5C7A',
  font:    "'Plus Jakarta Sans', system-ui, sans-serif",
  display: "'Space Grotesk', system-ui, sans-serif",
  mono:    "'JetBrains Mono', ui-monospace, monospace",
  r:  18,
  rSm: 12,
};

/* ── Agents ─────────────────────────────────────────────────── */
const AGENTS = [
  { id:'coordinator', name:'Coordinator',    short:'Coord',     code:'CO', color:'#7C5CFF', role:'Orchestrates the Band' },
  { id:'structure',   name:'Structure',      short:'Structure', code:'ST', color:'#22D3EE', role:'Maps the chapter' },
  { id:'pedagogy',    name:'Pedagogy',       short:'Pedagogy',  code:'PE', color:'#2EE59D', role:'Finds learner confusions' },
  { id:'brainstorm',  name:'Auto-Brainstorm',short:'Brainstorm',code:'BR', color:'#F6C85F', role:'Tests creative formats' },
  { id:'visual',      name:'Visual Builder', short:'Visual',    code:'VB', color:'#FF8BD1', role:'Builds the website' },
  { id:'verifier',    name:'Verifier',       short:'Verifier',  code:'VF', color:'#FF5C7A', role:'Checks faithfulness & safety' },
];
const AGENT = Object.fromEntries(AGENTS.map(a => [a.id, a]));

const SAMPLE = {
  book: 'Living Systems',
  chapter: 'Ch. 4 · Photosynthesis',
  blurb: 'How plants convert light into chemical energy.',
};

/* ── Icons (line, 24px, currentColor) ──────────────────────── */
function Icon({ name, size = 20, stroke = 1.7, style }) {
  const p = {
    doc:        <><rect x="5" y="3" width="14" height="18" rx="2.5"/><path d="M8.5 8h7M8.5 12h7M8.5 16h4"/></>,
    paste:      <><rect x="6" y="4" width="12" height="16" rx="2"/><rect x="9" y="2.5" width="6" height="3.2" rx="1.2"/></>,
    upload:     <><path d="M12 15V4"/><path d="M8 8l4-4 4 4"/><path d="M5 16v2.5A1.5 1.5 0 0 0 6.5 20h11a1.5 1.5 0 0 0 1.5-1.5V16"/></>,
    spark:      <><path d="M12 3l1.6 4.8L18.4 9.4 13.6 11 12 16l-1.6-5L5.6 9.4 10.4 7.8z"/><path d="M18.5 16.5l.7 2 .7-2 .7-.7-.7-.7-.7-2-.7 2-.7.7z"/></>,
    arrow:      <><path d="M5 12h13"/><path d="M13 6l6 6-6 6"/></>,
    check:      <><path d="M5 12.5l4.5 4.5L19 7"/></>,
    play:       <><path d="M8 5.5v13l11-6.5z"/></>,
    external:   <><path d="M14 5h5v5"/><path d="M19 5l-8 8"/><path d="M18 14v4.5A1.5 1.5 0 0 1 16.5 20h-9A1.5 1.5 0 0 1 6 18.5v-9A1.5 1.5 0 0 1 7.5 8H12"/></>,
    refresh:    <><path d="M19 9a7 7 0 1 0 .5 5"/><path d="M19 4v5h-5"/></>,
    chevron:    <><path d="M9 6l6 6-6 6"/></>,
    map:        <><path d="M4 7l5-2 6 2 5-2v12l-5 2-6-2-5 2z"/><path d="M9 5v14M15 7v14"/></>,
    book:       <><path d="M5 4.5A1.5 1.5 0 0 1 6.5 3H18v15H6.5A1.5 1.5 0 0 0 5 19.5z"/><path d="M5 19.5A1.5 1.5 0 0 0 6.5 21H18"/></>,
    bolt:       <><path d="M13 3L5 13h5l-1 8 8-11h-5z"/></>,
    shield:     <><path d="M12 3l7 2.5V11c0 4.5-3 7.5-7 9-4-1.5-7-4.5-7-9V5.5z"/><path d="M9 12l2 2 4-4"/></>,
    layers:     <><path d="M12 3l8 4.5-8 4.5-8-4.5z"/><path d="M4 12l8 4.5 8-4.5"/><path d="M4 16.5L12 21l8-4.5"/></>,
    brain:      <><path d="M9 4a3 3 0 0 0-3 3 3 3 0 0 0-1 5 3 3 0 0 0 2 4 3 3 0 0 0 3 2V4z"/><path d="M15 4a3 3 0 0 1 3 3 3 3 0 0 1 1 5 3 3 0 0 1-2 4 3 3 0 0 1-3 2V4z"/></>,
    target:     <><circle cx="12" cy="12" r="8"/><circle cx="12" cy="12" r="3.5"/></>,
    wand:       <><path d="M5 19l9-9"/><path d="M14.5 5.5l1 1M9 3v2M18 9.5l-1.5.5M6 9l-2 .5M19 15l-1.5.5"/><rect x="13" y="7" width="3" height="3" rx="0.8" transform="rotate(45 14.5 8.5)"/></>,
    search:     <><circle cx="11" cy="11" r="6"/><path d="M20 20l-4-4"/></>,
    eye:        <><path d="M2.5 12S6 5.5 12 5.5 21.5 12 21.5 12 18 18.5 12 18.5 2.5 12 2.5 12z"/><circle cx="12" cy="12" r="3"/></>,
    grid:       <><rect x="4" y="4" width="7" height="7" rx="1.5"/><rect x="13" y="4" width="7" height="7" rx="1.5"/><rect x="4" y="13" width="7" height="7" rx="1.5"/><rect x="13" y="13" width="7" height="7" rx="1.5"/></>,
    quiz:       <><circle cx="12" cy="12" r="8.5"/><path d="M9.5 9.5a2.5 2.5 0 1 1 3.2 2.4c-.7.3-.7.6-.7 1.1"/><path d="M12 16.5h.01"/></>,
    chat:       <><path d="M4 6.5A2.5 2.5 0 0 1 6.5 4h11A2.5 2.5 0 0 1 20 6.5v7A2.5 2.5 0 0 1 17.5 16H9l-4 4z"/></>,
    flame:      <><path d="M12 3c1 3-2 4-2 7a2 2 0 0 0 4 0c0 0 2 2 2 5a4 4 0 0 1-8 0c0-4 4-5 4-12z"/></>,
    clock:      <><circle cx="12" cy="12" r="8"/><path d="M12 8v4l3 2"/></>,
    route:      <><circle cx="6" cy="6" r="2.5"/><circle cx="18" cy="18" r="2.5"/><path d="M8.5 6H15a3 3 0 0 1 0 6H9a3 3 0 0 0 0 6h6.5"/></>,
    dots:       <><circle cx="6" cy="12" r="1.4"/><circle cx="12" cy="12" r="1.4"/><circle cx="18" cy="12" r="1.4"/></>,
    plus:       <><path d="M12 5v14M5 12h14"/></>,
    sliders:    <><path d="M5 8h9M18 8h1M5 16h1M10 16h9"/><circle cx="16" cy="8" r="2"/><circle cx="8" cy="16" r="2"/></>,
  }[name];
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none"
      stroke="currentColor" strokeWidth={stroke} strokeLinecap="round" strokeLinejoin="round"
      style={{ display:'block', flexShrink:0, ...style }}>{p}</svg>
  );
}

/* ── Primitives ────────────────────────────────────────────── */
function Btn({ children, variant='primary', icon, iconR, full, onClick, size='md', style }) {
  const h = size === 'lg' ? 56 : size === 'sm' ? 40 : 50;
  const base = {
    height:h, display:'inline-flex', alignItems:'center', justifyContent:'center',
    gap:9, padding: size==='sm' ? '0 16px' : '0 22px', borderRadius:14, cursor:'pointer',
    fontFamily:T.font, fontWeight:600, fontSize: size==='lg'?17:15, letterSpacing:0.1,
    border:'1px solid transparent', width: full?'100%':'auto', boxSizing:'border-box',
    transition:'transform .15s ease, background .2s ease, border-color .2s', userSelect:'none',
    WebkitTapHighlightColor:'transparent',
  };
  const vs = {
    primary: { background:T.primary, color:'#fff', boxShadow:'0 8px 26px -8px rgba(124,92,255,0.7)' },
    soft:    { background:T.primaryDim, color:'#C9BBFF', border:'1px solid rgba(124,92,255,0.32)' },
    ghost:   { background:'rgba(255,255,255,0.04)', color:T.text, border:`1px solid ${T.line}` },
    danger:  { background:'rgba(255,92,122,0.12)', color:'#FF8FA6', border:'1px solid rgba(255,92,122,0.32)' },
  }[variant];
  return (
    <button onClick={onClick} style={{ ...base, ...vs, ...style }}
      onMouseDown={e=>e.currentTarget.style.transform='scale(.97)'}
      onMouseUp={e=>e.currentTarget.style.transform=''}
      onMouseLeave={e=>e.currentTarget.style.transform=''}>
      {icon && <Icon name={icon} size={size==='lg'?20:18} />}
      {children}
      {iconR && <Icon name={iconR} size={size==='lg'?20:18} />}
    </button>
  );
}

function Card({ children, pad=18, style, glow, onClick }) {
  return (
    <div onClick={onClick} style={{
      background:T.surface, border:`1px solid ${T.line}`, borderRadius:T.r,
      padding:pad, boxSizing:'border-box', position:'relative',
      boxShadow: glow ? `0 0 0 1px ${glow}22, 0 16px 40px -24px ${glow}` : '0 14px 34px -28px rgba(0,0,0,0.9)',
      ...style }}>{children}</div>
  );
}

function Label({ children, color=T.text3, dot, style }) {
  return (
    <div style={{ display:'flex', alignItems:'center', gap:7, fontFamily:T.mono,
      fontSize:11, fontWeight:600, letterSpacing:1.4, textTransform:'uppercase', color, ...style }}>
      {dot && <span style={{ width:6, height:6, borderRadius:'50%', background:dot,
        boxShadow:`0 0 8px ${dot}` }} />}
      {children}
    </div>
  );
}

function StatusDot({ status, color, size=8 }) {
  const map = {
    waiting:   { c:T.text3, pulse:false },
    active:    { c:color,   pulse:true  },
    completed: { c:T.success,pulse:false },
    warning:   { c:T.warn,  pulse:false },
    failed:    { c:T.error, pulse:false },
  }[status] || { c:T.text3 };
  return (
    <span style={{ position:'relative', width:size, height:size, flexShrink:0 }}>
      <span style={{ position:'absolute', inset:0, borderRadius:'50%', background:map.c,
        boxShadow:`0 0 8px ${map.c}` }} />
      {map.pulse && <span style={{ position:'absolute', inset:0, borderRadius:'50%',
        background:map.c, animation:'cs-ping 1.5s ease-out infinite' }} />}
    </span>
  );
}

/* Ring avatar with agent initials */
function Avatar({ agent, size=42, status='waiting', active }) {
  const a = typeof agent === 'string' ? AGENT[agent] : agent;
  const ringOn = active || status==='active' || status==='completed';
  return (
    <span style={{ position:'relative', width:size, height:size, display:'inline-flex',
      alignItems:'center', justifyContent:'center', flexShrink:0 }}>
      <span style={{ position:'absolute', inset:0, borderRadius:'50%',
        border:`1.6px solid ${ringOn ? a.color : T.lineHi}`,
        boxShadow: active ? `0 0 0 4px ${a.color}1f, 0 0 18px ${a.color}66` : 'none',
        transition:'all .3s' }} />
      {active && <span style={{ position:'absolute', inset:-3, borderRadius:'50%',
        border:`1.5px solid ${a.color}`, opacity:0.5, animation:'cs-ring 1.8s ease-out infinite' }} />}
      <span style={{ fontFamily:T.mono, fontWeight:700, fontSize:size*0.32,
        color: ringOn ? a.color : T.text2, letterSpacing:0.5 }}>{a.code}</span>
      {(status==='completed') && (
        <span style={{ position:'absolute', right:-1, bottom:-1, width:size*0.36, height:size*0.36,
          borderRadius:'50%', background:T.success, border:`2px solid ${T.bg}`,
          display:'flex', alignItems:'center', justifyContent:'center', color:'#06291c' }}>
          <Icon name="check" size={size*0.22} stroke={3}/></span>
      )}
    </span>
  );
}

function Progress({ value, color=T.primary, height=6, track='rgba(255,255,255,0.07)', glow=true }) {
  return (
    <div style={{ height, borderRadius:height, background:track, overflow:'hidden', position:'relative' }}>
      <div style={{ position:'absolute', inset:0, width:`${Math.max(0,Math.min(100,value))}%`,
        background:`linear-gradient(90deg, ${color}cc, ${color})`, borderRadius:height,
        boxShadow: glow ? `0 0 14px ${color}aa` : 'none', transition:'width .6s cubic-bezier(.4,0,.2,1)' }}>
        <div style={{ position:'absolute', inset:0, background:'rgba(255,255,255,0.25)',
          mixBlendMode:'overlay', animation:'cs-shim 1.8s linear infinite',
          maskImage:'linear-gradient(90deg,transparent, #000, transparent)' }} />
      </div>
    </div>
  );
}

/* Pill option group */
function PillGroup({ options, value, onChange, color=T.primary, small }) {
  return (
    <div style={{ display:'flex', flexWrap:'wrap', gap:8 }}>
      {options.map(o => {
        const v = typeof o === 'string' ? o : o.v;
        const lab = typeof o === 'string' ? o : o.label;
        const on = v === value;
        return (
          <button key={v} onClick={()=>onChange&&onChange(v)} style={{
            padding: small ? '8px 14px' : '11px 16px', borderRadius:12, cursor:'pointer',
            fontFamily:T.font, fontSize:small?13:14, fontWeight:600,
            background: on ? `${color}1f` : 'rgba(255,255,255,0.03)',
            color: on ? '#fff' : T.text2,
            border:`1px solid ${on ? color+'88' : T.line}`,
            boxShadow: on ? `0 0 16px -4px ${color}99` : 'none',
            transition:'all .18s', WebkitTapHighlightColor:'transparent',
          }}>{lab}</button>
        );
      })}
    </div>
  );
}

function Toggle({ on, onChange }) {
  return (
    <button onClick={()=>onChange&&onChange(!on)} style={{
      width:46, height:27, borderRadius:14, border:'none', cursor:'pointer', position:'relative',
      background: on ? T.primary : 'rgba(255,255,255,0.12)', transition:'background .22s', flexShrink:0,
      boxShadow: on ? `0 0 16px -2px ${T.primary}aa` : 'none', padding:0,
    }}>
      <span style={{ position:'absolute', top:3, left: on ? 22 : 3, width:21, height:21, borderRadius:'50%',
        background:'#fff', transition:'left .22s cubic-bezier(.4,0,.2,1)', boxShadow:'0 2px 6px rgba(0,0,0,.4)' }} />
    </button>
  );
}

/* Screen scaffold: dark canvas filling the device content area */
function Screen({ children, pad=20, gap=16, scroll=true, bg, style }) {
  return (
    <div style={{
      minHeight:'100%', background: bg || `radial-gradient(120% 60% at 50% -10%, #161E33 0%, ${T.bg} 55%)`,
      color:T.text, fontFamily:T.font, display:'flex', flexDirection:'column', gap,
      padding:pad, boxSizing:'border-box', ...style }}>{children}</div>
  );
}

/* Top bar used inside screens */
function TopBar({ title, sub, left, right, brand }) {
  return (
    <div style={{ display:'flex', alignItems:'center', gap:12, minHeight:36 }}>
      {brand ? <Logo /> : left}
      <div style={{ flex:1, minWidth:0 }}>
        {title && <div style={{ fontFamily:T.display, fontWeight:600, fontSize:18, color:T.text,
          whiteSpace:'nowrap', overflow:'hidden', textOverflow:'ellipsis' }}>{title}</div>}
        {sub && <div style={{ fontSize:12.5, color:T.text2, marginTop:1 }}>{sub}</div>}
      </div>
      {right}
    </div>
  );
}

function Logo({ size=22 }) {
  return (
    <div style={{ display:'flex', alignItems:'center', gap:9 }}>
      <span style={{ position:'relative', width:size, height:size, display:'inline-flex',
        alignItems:'center', justifyContent:'center' }}>
        <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
          <circle cx="12" cy="12" r="3" fill={T.primary}/>
          <circle cx="5" cy="6" r="1.7" fill="#22D3EE"/>
          <circle cx="19" cy="7" r="1.7" fill="#2EE59D"/>
          <circle cx="6" cy="18" r="1.7" fill="#F6C85F"/>
          <circle cx="18" cy="18" r="1.7" fill="#FF8BD1"/>
          <g stroke="rgba(255,255,255,0.22)" strokeWidth="1">
            <path d="M12 12L5 6M12 12l7-5M12 12l-6 6M12 12l6 6"/>
          </g>
        </svg>
      </span>
      <span style={{ fontFamily:T.display, fontWeight:600, fontSize:16, letterSpacing:-0.2 }}>
        Chapter<span style={{ color:T.primary }}>Stage</span></span>
    </div>
  );
}

/* Inject global keyframes + font smoothing once */
function injectCSS() {
  if (document.getElementById('cs-css')) return;
  const s = document.createElement('style');
  s.id = 'cs-css';
  s.textContent = `
    @keyframes cs-ping { 0%{transform:scale(1);opacity:.7} 80%,100%{transform:scale(2.4);opacity:0} }
    @keyframes cs-ring { 0%{transform:scale(1);opacity:.5} 100%{transform:scale(1.5);opacity:0} }
    @keyframes cs-shim { 0%{transform:translateX(-100%)} 100%{transform:translateX(100%)} }
    @keyframes cs-up { from{opacity:0;transform:translateY(10px)} to{opacity:1;transform:none} }
    @keyframes cs-fade { from{opacity:0} to{opacity:1} }
    @keyframes cs-float { 0%,100%{transform:translateY(0)} 50%{transform:translateY(-5px)} }
    @keyframes cs-pulseGlow { 0%,100%{opacity:.5} 50%{opacity:1} }
    @keyframes cs-dash { to { stroke-dashoffset:-16 } }
    .cs-up { animation: cs-up .5s cubic-bezier(.2,.7,.2,1) both; }
    .cs-fade { animation: cs-fade .5s ease both; }
    .cs-scroll::-webkit-scrollbar{ width:0; height:0; }
    * { -webkit-font-smoothing:antialiased; }
  `;
  document.head.appendChild(s);
}
injectCSS();

Object.assign(window, {
  T, AGENTS, AGENT, SAMPLE, Icon, Btn, Card, Label, StatusDot, Avatar,
  Progress, PillGroup, Toggle, Screen, TopBar, Logo,
});
