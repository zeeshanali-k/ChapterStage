/* ChapterStage — agent strip variants: chips · constellation · rail
   Props: variant, statuses {id:status}, activeId, compact */
const { useState: useStateS } = React;

function BandRoomBadge({ live, style }) {
  return (
    <div style={{ display:'inline-flex', alignItems:'center', gap:8, padding:'6px 12px 6px 8px',
      borderRadius:999, background:'rgba(124,92,255,0.10)', border:'1px solid rgba(124,92,255,0.28)', ...style }}>
      <span style={{ position:'relative', width:18, height:18, display:'inline-flex', alignItems:'center', justifyContent:'center' }}>
        <Icon name="route" size={13} style={{ color:T.primary }}/>
        {live && <span style={{ position:'absolute', inset:-3, borderRadius:'50%', border:`1.4px solid ${T.primary}`,
          opacity:.6, animation:'cs-ring 1.8s ease-out infinite' }}/>}
      </span>
      <span style={{ fontFamily:T.mono, fontSize:11, fontWeight:600, letterSpacing:1, color:'#C9BBFF' }}>BAND ROOM</span>
      {live && <span style={{ fontFamily:T.mono, fontSize:10, color:T.success, letterSpacing:.5 }}>● LIVE</span>}
    </div>
  );
}

/* ── Variant: CHIPS ─ horizontal hand-off row ─────────────── */
function ChipStrip({ statuses, activeId }) {
  return (
    <div className="cs-scroll" style={{ display:'flex', alignItems:'center', gap:0, overflowX:'auto', paddingBottom:2 }}>
      {AGENTS.map((a, i) => {
        const st = statuses[a.id] || 'waiting';
        const on = a.id === activeId;
        return (
          <React.Fragment key={a.id}>
            <div style={{ display:'flex', flexDirection:'column', alignItems:'center', gap:6, minWidth:62 }}>
              <Avatar agent={a} size={38} status={st} active={on}/>
              <div style={{ textAlign:'center' }}>
                <div style={{ fontSize:10.5, fontWeight:600, color: st==='waiting'?T.text3:T.text,
                  whiteSpace:'nowrap' }}>{a.short}</div>
                <div style={{ fontFamily:T.mono, fontSize:8.5, letterSpacing:.5, textTransform:'uppercase',
                  color: on ? a.color : st==='completed'?T.success:T.text3 }}>
                  {on ? 'working' : st==='completed' ? 'done' : st==='waiting' ? '—' : st}</div>
              </div>
            </div>
            {i < AGENTS.length-1 && (
              <div style={{ width:18, height:1.5, margin:'0 1px', alignSelf:'flex-start', marginTop:18,
                background: statuses[AGENTS[i+1].id] && statuses[AGENTS[i+1].id]!=='waiting'
                  ? `linear-gradient(90deg, ${a.color}, ${AGENTS[i+1].color})` : T.lineHi,
                borderRadius:2, flexShrink:0 }}/>
            )}
          </React.Fragment>
        );
      })}
    </div>
  );
}

/* ── Variant: CONSTELLATION ─ spatial Band room ───────────── */
const CONST_POS = [
  { x:150, y:26 }, { x:248, y:66 }, { x:248, y:146 },
  { x:150, y:186 }, { x:52, y:146 }, { x:52, y:66 },
];
function Constellation({ statuses, activeId, height=212 }) {
  const cx = 150, cy = 106;
  return (
    <div style={{ position:'relative', width:300, height, margin:'0 auto' }}>
      <svg width="300" height={height} style={{ position:'absolute', inset:0 }}>
        {AGENTS.map((a,i)=>{
          const p = CONST_POS[i]; const st = statuses[a.id]||'waiting';
          const lit = st!=='waiting';
          return <line key={a.id} x1={cx} y1={cy} x2={p.x} y2={p.y}
            stroke={lit ? a.color : 'rgba(255,255,255,0.09)'} strokeWidth={a.id===activeId?2:1.2}
            strokeDasharray={a.id===activeId? '4 4':'0'} strokeLinecap="round"
            style={{ opacity: lit?0.7:0.5, animation: a.id===activeId?'cs-dash .6s linear infinite':'none' }}/>;
        })}
      </svg>
      {/* center hub */}
      <div style={{ position:'absolute', left:cx, top:cy, transform:'translate(-50%,-50%)',
        width:44, height:44, borderRadius:'50%', background:'radial-gradient(circle, rgba(124,92,255,0.45), rgba(124,92,255,0.08))',
        border:'1.5px solid rgba(124,92,255,0.5)', display:'flex', alignItems:'center', justifyContent:'center' }}>
        <Icon name="route" size={20} style={{ color:'#C9BBFF' }}/>
        <span style={{ position:'absolute', inset:-4, borderRadius:'50%', border:'1.4px solid rgba(124,92,255,0.4)',
          animation:'cs-ring 2.2s ease-out infinite' }}/>
      </div>
      {AGENTS.map((a,i)=>{
        const p = CONST_POS[i]; const st = statuses[a.id]||'waiting';
        const on = a.id===activeId;
        return (
          <div key={a.id} style={{ position:'absolute', left:p.x, top:p.y, transform:'translate(-50%,-50%)',
            display:'flex', flexDirection:'column', alignItems:'center', gap:3,
            animation: on ? 'cs-float 2.6s ease-in-out infinite' : 'none' }}>
            <Avatar agent={a} size={on?44:38} status={st} active={on}/>
            <span style={{ fontSize:9, fontWeight:600, color: st==='waiting'?T.text3:T.text2,
              fontFamily:T.mono, letterSpacing:.3, whiteSpace:'nowrap' }}>{a.short}</span>
          </div>
        );
      })}
    </div>
  );
}

/* ── Variant: RAIL ─ vertical stepper ─────────────────────── */
function Rail({ statuses, activeId }) {
  return (
    <div style={{ position:'relative', paddingLeft:4 }}>
      {AGENTS.map((a,i)=>{
        const st = statuses[a.id]||'waiting';
        const on = a.id===activeId;
        const nextLit = i<AGENTS.length-1 && (statuses[AGENTS[i+1].id]||'waiting')!=='waiting';
        return (
          <div key={a.id} style={{ display:'flex', gap:14, alignItems:'stretch' }}>
            <div style={{ display:'flex', flexDirection:'column', alignItems:'center', width:42 }}>
              <Avatar agent={a} size={36} status={st} active={on}/>
              {i<AGENTS.length-1 && (
                <div style={{ flex:1, width:2, minHeight:18, marginTop:4, marginBottom:4, borderRadius:2,
                  background: nextLit ? a.color : T.lineHi, opacity: nextLit?0.6:1 }}/>
              )}
            </div>
            <div style={{ flex:1, paddingBottom:14, paddingTop:4 }}>
              <div style={{ display:'flex', alignItems:'center', gap:8 }}>
                <span style={{ fontSize:14, fontWeight:700, color: st==='waiting'?T.text3:T.text }}>{a.name}</span>
                <span style={{ fontFamily:T.mono, fontSize:9, letterSpacing:.6, textTransform:'uppercase',
                  padding:'2px 7px', borderRadius:6,
                  color: on?a.color : st==='completed'?T.success : T.text3,
                  background: on?`${a.color}1c` : st==='completed'?'rgba(46,229,157,0.12)':'rgba(255,255,255,0.04)' }}>
                  {on?'working':st==='completed'?'done':st==='waiting'?'queued':st}</span>
              </div>
              <div style={{ fontSize:12, color: on?T.text2:T.text3, marginTop:2 }}>
                {on ? <span style={{ display:'inline-flex', alignItems:'center', gap:6 }}>
                  {a.role}<Dots color={a.color}/></span> : a.role}
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
}
function Dots({ color }) {
  return (
    <span style={{ display:'inline-flex', gap:3 }}>
      {[0,1,2].map(i=><span key={i} style={{ width:3, height:3, borderRadius:'50%', background:color,
        animation:`cs-pulseGlow 1.2s ease-in-out ${i*0.18}s infinite` }}/>)}
    </span>
  );
}

function AgentStrip({ variant='chips', statuses={}, activeId }) {
  if (variant==='constellation') return <Constellation statuses={statuses} activeId={activeId}/>;
  if (variant==='rail') return <Rail statuses={statuses} activeId={activeId}/>;
  return <ChipStrip statuses={statuses} activeId={activeId}/>;
}

Object.assign(window, { AgentStrip, ChipStrip, Constellation, Rail, BandRoomBadge, Dots });
