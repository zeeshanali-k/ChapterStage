/* ChapterStage — screens 2/2: Generation Progress (feed · focus) + Experience Viewer */
const { useState: useS2 } = React;

/* event type → accent (mostly agent color, a few semantic) */
function typeColor(type, agentColor) {
  if (type==='Verified' || type==='Selected') return T.success;
  if (type==='Rejected') return T.error;
  if (type==='Published') return T.primary;
  return agentColor;
}
function EventRow({ ev, i, dense }) {
  const a = AGENT[ev.agent];
  const tc = typeColor(ev.type, a.color);
  return (
    <div className="cs-up" style={{ display:'flex', gap:11, animationDelay:`${Math.min(i,1)*40}ms` }}>
      <div style={{ display:'flex', flexDirection:'column', alignItems:'center', paddingTop:3 }}>
        <span style={{ width:9, height:9, borderRadius:'50%', background:a.color,
          boxShadow:`0 0 8px ${a.color}`, flexShrink:0 }}/>
        {!dense && <span style={{ width:1.5, flex:1, marginTop:4, background:T.line, borderRadius:2 }}/>}
      </div>
      <div style={{ flex:1, paddingBottom: dense?0:13 }}>
        <div style={{ display:'flex', alignItems:'center', gap:7, flexWrap:'wrap' }}>
          <span style={{ fontSize:12.5, fontWeight:700, color:a.color }}>{a.name}</span>
          <span style={{ fontFamily:T.mono, fontSize:9.5, letterSpacing:.6, textTransform:'uppercase',
            padding:'2px 6px', borderRadius:5, color:tc, background:`${tc}1c` }}>{ev.type}</span>
          <span style={{ flex:1 }}/>
          <span style={{ fontFamily:T.mono, fontSize:10, color:T.text3 }}>{ev.t}</span>
        </div>
        <div style={{ fontSize:13.5, fontWeight:600, color:T.text, marginTop:3 }}>{ev.title}</div>
        <div style={{ fontSize:12.5, color:T.text2, marginTop:2, lineHeight:1.45 }}>{ev.msg}</div>
        {ev.payload && (
          <div style={{ marginTop:7, fontFamily:T.mono, fontSize:11, color:T.text2, padding:'7px 10px',
            borderRadius:8, background:'rgba(255,255,255,0.03)', border:`1px solid ${T.line}` }}>{ev.payload}</div>
        )}
      </div>
    </div>
  );
}

function ActiveCard({ activeId, events, big }) {
  if (!activeId) return null;
  const a = AGENT[activeId];
  const last = [...events].reverse().find(e=>e.agent===activeId) || {};
  return (
    <Card pad={16} glow={a.color} style={{ display:'flex', gap:13, alignItems:'flex-start',
      background:`linear-gradient(180deg, ${a.color}14, ${T.surface} 70%)` }}>
      <Avatar agent={a} size={big?52:46} status="active" active/>
      <div style={{ flex:1, minWidth:0 }}>
        <div style={{ display:'flex', alignItems:'center', gap:8 }}>
          <span style={{ fontFamily:T.display, fontWeight:600, fontSize:big?17:15.5, color:T.text }}>{a.name}</span>
          <span style={{ display:'inline-flex', alignItems:'center', gap:5, fontFamily:T.mono, fontSize:10,
            color:a.color, letterSpacing:.5 }}><Dots color={a.color}/>WORKING</span>
        </div>
        <div style={{ fontSize:13.5, color:T.text2, marginTop:5, lineHeight:1.5 }}>
          {last.msg ? `${last.title} — ${last.msg}` : a.role}</div>
      </div>
    </Card>
  );
}

function RingProgress({ value, size=92 }) {
  const r=(size-10)/2, c=2*Math.PI*r;
  return (
    <div style={{ position:'relative', width:size, height:size }}>
      <svg width={size} height={size} style={{ transform:'rotate(-90deg)' }}>
        <circle cx={size/2} cy={size/2} r={r} fill="none" stroke="rgba(255,255,255,0.08)" strokeWidth="5"/>
        <circle cx={size/2} cy={size/2} r={r} fill="none" stroke={T.primary} strokeWidth="5" strokeLinecap="round"
          strokeDasharray={c} strokeDashoffset={c*(1-value/100)}
          style={{ transition:'stroke-dashoffset .6s cubic-bezier(.4,0,.2,1)', filter:`drop-shadow(0 0 5px ${T.primary}aa)` }}/>
      </svg>
      <div style={{ position:'absolute', inset:0, display:'flex', flexDirection:'column', alignItems:'center', justifyContent:'center' }}>
        <span style={{ fontFamily:T.mono, fontWeight:700, fontSize:22, color:T.text }}>{Math.round(value)}</span>
        <span style={{ fontFamily:T.mono, fontSize:9, color:T.text3, letterSpacing:1 }}>PERCENT</span>
      </div>
    </div>
  );
}

function CompletedCard({ go, settings }) {
  const [trace,setTrace]=useS2(false);
  return (
    <Card pad={0} glow={T.success} style={{ overflow:'hidden' }} >
      <div style={{ padding:18, background:'radial-gradient(120% 120% at 50% 0%, rgba(46,229,157,0.20), transparent 60%)' }}>
        <div style={{ display:'flex', alignItems:'center', gap:11, marginBottom:13 }}>
          <div style={{ width:44, height:44, borderRadius:'50%', background:'rgba(46,229,157,0.16)',
            border:'1px solid rgba(46,229,157,0.4)', color:T.success, display:'flex', alignItems:'center', justifyContent:'center' }}>
            <Icon name="check" size={24} stroke={2.4}/></div>
          <div>
            <Label dot={T.success} color={T.success}>COMPLETE</Label>
            <div style={{ fontFamily:T.display, fontWeight:600, fontSize:19, marginTop:3 }}>Your chapter is ready</div>
          </div>
        </div>
        {/* link chip */}
        <div style={{ display:'flex', alignItems:'center', gap:9, padding:'11px 13px', borderRadius:11,
          background:'rgba(255,255,255,0.04)', border:`1px solid ${T.line}` }}>
          <Icon name="external" size={15} style={{ color:T.text3 }}/>
          <span style={{ fontFamily:T.mono, fontSize:12, color:T.text2, flex:1, whiteSpace:'nowrap', overflow:'hidden', textOverflow:'ellipsis' }}>
            chapterstage.app/c/photosynthesis</span>
          <span style={{ fontFamily:T.mono, fontSize:10.5, color:T.success }}>LIVE</span>
        </div>
        <div style={{ display:'flex', gap:8, marginTop:12 }}>
          {[['SCENES',settings.screens],['FAITHFUL','0.96'],['SAFETY','PASS']].map(([k,v])=>(
            <div key={k} style={{ flex:1, padding:'9px 0', textAlign:'center', borderRadius:10,
              background:'rgba(255,255,255,0.03)', border:`1px solid ${T.line}` }}>
              <div style={{ fontFamily:T.mono, fontWeight:700, fontSize:15, color:T.text }}>{v}</div>
              <div style={{ fontFamily:T.mono, fontSize:9, color:T.text3, letterSpacing:.8, marginTop:2 }}>{k}</div>
            </div>
          ))}
        </div>
        <div style={{ display:'flex', flexDirection:'column', gap:9, marginTop:15 }}>
          <Btn full size="lg" iconR="arrow" onClick={()=>go('viewer')}>Open Interactive Chapter</Btn>
          <Btn full variant="ghost" icon="route" onClick={()=>setTrace(t=>!t)}>
            {trace?'Hide Agent Trace':'View Agent Trace'}</Btn>
        </div>
      </div>
      {trace && (
        <div style={{ padding:'4px 18px 18px' }}>
          <Label style={{ margin:'6px 0 12px' }}>AGENT TRACE · {window.CS_EVENTS.length} EVENTS</Label>
          {window.CS_EVENTS.map((ev,i)=><EventRow key={i} ev={ev} i={i}/>)}
        </div>
      )}
    </Card>
  );
}

function ProgressScreen({ statuses={}, activeId, events=[], progress=0, done, elapsed=0,
  layoutVariant='feed', stripVariant='chips', go, settings }) {
  const mm = String(Math.floor(elapsed/60)).padStart(2,'0');
  const ss = String(elapsed%60).padStart(2,'0');

  const Header = (
    <div style={{ display:'flex', alignItems:'center', gap:10 }}>
      <BackBtn onClick={()=>go('settings')}/>
      <div style={{ flex:1 }}>
        <div style={{ fontFamily:T.display, fontWeight:600, fontSize:17 }}>
          {done?'Workflow complete':'Agents collaborating'}</div>
        <div style={{ fontSize:12, color:T.text2, fontFamily:T.mono }}>{SAMPLE.chapter}</div>
      </div>
      <div style={{ fontFamily:T.mono, fontSize:13, color:done?T.success:T.text2, display:'flex', alignItems:'center', gap:6 }}>
        <Icon name="clock" size={14}/>{mm}:{ss}</div>
    </div>
  );

  if (layoutVariant==='focus') {
    const latest = events[events.length-1];
    return (
      <Screen gap={16}>
        {Header}
        <div style={{ display:'flex', alignItems:'center', justifyContent:'space-between' }}>
          <BandRoomBadge live={!done}/>
          <span style={{ fontFamily:T.mono, fontSize:11, color:T.text3 }}>
            {Object.values(statuses).filter(s=>s==='completed').length}/6 done</span>
        </div>
        <Card pad={14} style={{ background:'radial-gradient(120% 90% at 50% 0%, rgba(124,92,255,0.12), transparent 60%)' }}>
          <Constellation statuses={statuses} activeId={activeId}/>
          <div style={{ display:'flex', alignItems:'center', gap:16, marginTop:6 }}>
            <RingProgress value={progress}/>
            <div style={{ flex:1 }}>
              {done ? (
                <div style={{ color:T.success, fontWeight:600, fontSize:14, display:'flex', alignItems:'center', gap:7 }}>
                  <Icon name="check" size={17}/>All agents finished</div>
              ) : latest ? (
                <div>
                  <Label color={T.text3} style={{ marginBottom:6 }}>LATEST</Label>
                  <div style={{ fontSize:13, fontWeight:600 }}>{latest.title}</div>
                  <div style={{ fontSize:12, color:T.text2, marginTop:2, lineHeight:1.4 }}>{latest.msg}</div>
                </div>
              ) : null}
            </div>
          </div>
        </Card>
        {done ? <CompletedCard go={go} settings={settings}/> : <ActiveCard activeId={activeId} events={events} big/>}
      </Screen>
    );
  }

  /* feed layout (default) */
  return (
    <Screen gap={15}>
      {Header}
      <Card pad={15}>
        <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:10 }}>
          <BandRoomBadge live={!done}/>
          <span style={{ fontFamily:T.mono, fontSize:14, fontWeight:700, color:done?T.success:T.primary }}>
            {Math.round(progress)}%</span>
        </div>
        <Progress value={progress} color={done?T.success:T.primary}/>
        <div style={{ marginTop:16 }}>
          <AgentStrip variant={stripVariant} statuses={statuses} activeId={activeId}/>
        </div>
      </Card>

      {done ? <CompletedCard go={go} settings={settings}/> : <ActiveCard activeId={activeId} events={events}/>}

      <div>
        <div style={{ display:'flex', alignItems:'center', justifyContent:'space-between', marginBottom:12 }}>
          <Label dot={!done?T.primary:T.success}>LIVE TRACE</Label>
          <span style={{ fontFamily:T.mono, fontSize:11, color:T.text3 }}>{events.length} events</span>
        </div>
        <div style={{ display:'flex', flexDirection:'column' }}>
          {[...events].reverse().map((ev,i)=>(
            <EventRow key={events.length-1-i} ev={ev} i={i} dense={i===events.length-1}/>
          ))}
          {events.length===0 && <div style={{ fontSize:13, color:T.text3 }}>Waiting for the first hand-off…</div>}
        </div>
      </div>
    </Screen>
  );
}

/* ── 6.7 Experience Viewer ─────────────────────────────────── */
function ViewerScreen({ state='loaded', go, onRetry }) {
  return (
    <div style={{ minHeight:'100%', background:T.bg, display:'flex', flexDirection:'column' }}>
      {/* app top bar */}
      <div style={{ display:'flex', alignItems:'center', gap:10, padding:'14px 16px',
        borderBottom:`1px solid ${T.line}`, color:T.text, fontFamily:T.font }}>
        <BackBtn onClick={()=>go('progress')}/>
        <div style={{ flex:1, minWidth:0 }}>
          <div style={{ fontWeight:600, fontSize:15, whiteSpace:'nowrap', overflow:'hidden', textOverflow:'ellipsis' }}>
            Photosynthesis</div>
          <div style={{ fontSize:11.5, color:T.text3, fontFamily:T.mono }}>Living Systems · Ch.4</div>
        </div>
        <button style={{ width:38, height:38, borderRadius:11, background:'rgba(255,255,255,0.04)',
          border:`1px solid ${T.line}`, color:T.text2, cursor:'pointer', display:'flex', alignItems:'center', justifyContent:'center' }}>
          <Icon name="external" size={18}/></button>
      </div>
      {/* browser chrome */}
      <div style={{ display:'flex', alignItems:'center', gap:8, padding:'8px 14px', background:'#0E1320',
        borderBottom:`1px solid ${T.line}` }}>
        <div style={{ display:'flex', gap:5 }}>
          {['#FF5C7A','#F6C85F','#2EE59D'].map(c=><span key={c} style={{ width:8, height:8, borderRadius:'50%', background:c, opacity:.7 }}/>)}
        </div>
        <div style={{ flex:1, fontFamily:T.mono, fontSize:11, color:T.text3, textAlign:'center',
          background:'rgba(255,255,255,0.03)', borderRadius:7, padding:'4px 0' }}>
          chapterstage.app/c/photosynthesis</div>
      </div>
      {/* webview body */}
      <div style={{ flex:1, position:'relative', overflow:'auto' }}>
        {state==='loading' && <ViewerLoading/>}
        {state==='error'   && <ViewerError onRetry={onRetry}/>}
        {state==='loaded'  && <GeneratedScene/>}
      </div>
    </div>
  );
}
function ViewerLoading() {
  return (
    <div style={{ padding:22, fontFamily:T.font }}>
      <div style={{ display:'flex', justifyContent:'center', gap:6, marginBottom:24 }}>
        {[0,1,2,3].map(i=><span key={i} style={{ width:24, height:4, borderRadius:2,
          background:i===0?T.primary:'rgba(255,255,255,0.1)' }}/>)}
      </div>
      {[180,22,'70%','90%','60%',140].map((h,i)=>(
        <div key={i} style={{ height:typeof h==='number'?h:14, width:typeof h==='string'?h:'100%',
          borderRadius:typeof h==='number'&&h>100?16:6, marginBottom:14, background:'rgba(255,255,255,0.05)',
          position:'relative', overflow:'hidden' }}>
          <div style={{ position:'absolute', inset:0, background:'linear-gradient(90deg,transparent,rgba(255,255,255,0.07),transparent)',
            animation:'cs-shim 1.4s linear infinite' }}/></div>
      ))}
      <div style={{ textAlign:'center', fontFamily:T.mono, fontSize:11.5, color:T.text3, marginTop:8 }}>
        Loading interactive chapter…</div>
    </div>
  );
}
function ViewerError({ onRetry }) {
  return (
    <div style={{ height:'100%', display:'flex', flexDirection:'column', alignItems:'center', justifyContent:'center',
      gap:14, padding:30, fontFamily:T.font, textAlign:'center' }}>
      <div style={{ width:56, height:56, borderRadius:16, background:'rgba(255,92,122,0.12)', color:'#FF8FA6',
        border:'1px solid rgba(255,92,122,0.3)', display:'flex', alignItems:'center', justifyContent:'center' }}>
        <Icon name="refresh" size={26}/></div>
      <div>
        <div style={{ fontWeight:600, fontSize:16, color:T.text }}>Couldn’t load the experience</div>
        <div style={{ fontSize:13, color:T.text2, marginTop:5, maxWidth:230, lineHeight:1.5 }}>
          The hosted chapter didn’t respond. Check your connection and try again.</div>
      </div>
      <Btn icon="refresh" onClick={onRetry}>Retry</Btn>
    </div>
  );
}

/* The generated mini-site, rendered inside the WebView (its own identity) */
function GeneratedScene() {
  const G = '#3DDC97';
  return (
    <div style={{ minHeight:'100%', fontFamily:T.font, color:'#0c1410',
      background:'linear-gradient(170deg, #0c1f18 0%, #0a1712 100%)', padding:'0 0 20px' }}>
      {/* progress */}
      <div style={{ display:'flex', gap:5, padding:'18px 20px 4px' }}>
        {Array.from({length:8}).map((_,i)=><span key={i} style={{ flex:1, height:4, borderRadius:2,
          background:i===0?G:i<1?G:'rgba(255,255,255,0.12)' }}/>)}
      </div>
      <div style={{ padding:'10px 22px 0' }}>
        <div style={{ fontFamily:T.mono, fontSize:11, letterSpacing:1.5, color:G, textTransform:'uppercase' }}>
          Scene 01 · Hook</div>
        <h2 style={{ fontFamily:T.display, fontWeight:600, fontSize:25, color:'#EAF7F0', margin:'8px 0 0',
          lineHeight:1.15, letterSpacing:-0.4 }}>How a leaf eats light</h2>
      </div>
      {/* visual/diagram */}
      <div style={{ margin:'18px 20px', borderRadius:18, padding:'24px 14px',
        background:'radial-gradient(120% 90% at 50% 0%, rgba(61,220,151,0.16), rgba(255,255,255,0.02))',
        border:'1px solid rgba(61,220,151,0.22)' }}>
        <svg viewBox="0 0 280 130" style={{ width:'100%', display:'block' }}>
          {/* sun */}
          <g>
            <circle cx="42" cy="46" r="18" fill="#F6C85F" opacity="0.9"/>
            {Array.from({length:8}).map((_,i)=>{const an=i*Math.PI/4;return(
              <line key={i} x1={42+Math.cos(an)*23} y1={46+Math.sin(an)*23} x2={42+Math.cos(an)*30} y2={46+Math.sin(an)*30}
                stroke="#F6C85F" strokeWidth="2.5" strokeLinecap="round"/>);})}
          </g>
          <path d="M66 50 Q100 56 120 64" stroke="#F6C85F" strokeWidth="2" strokeDasharray="4 4" fill="none" opacity="0.7"/>
          {/* leaf */}
          <path d="M140 38 C172 30 196 50 188 84 C156 92 130 74 140 38 Z" fill="#3DDC97" opacity="0.92"/>
          <path d="M150 78 C156 64 168 52 184 46" stroke="#0c1f18" strokeWidth="2" fill="none" opacity="0.5"/>
          {/* CO2 + water in */}
          <text x="120" y="112" fontFamily="ui-monospace,monospace" fontSize="11" fill="#9fe8c8">CO₂ + H₂O</text>
          {/* glucose out */}
          <path d="M196 64 Q224 64 244 60" stroke="#3DDC97" strokeWidth="2" strokeDasharray="4 4" fill="none"/>
          <g transform="translate(250,58)">
            <circle r="13" fill="rgba(61,220,151,0.18)" stroke="#3DDC97" strokeWidth="1.5"/>
            <text x="0" y="4" textAnchor="middle" fontFamily="ui-monospace,monospace" fontSize="11" fill="#EAF7F0">C₆</text>
          </g>
        </svg>
      </div>
      {/* explanation card */}
      <div style={{ margin:'0 20px', padding:16, borderRadius:16, background:'rgba(255,255,255,0.05)',
        border:'1px solid rgba(255,255,255,0.08)' }}>
        <div style={{ fontSize:14.5, lineHeight:1.55, color:'#D6E8E0' }}>
          A leaf takes in <b style={{ color:'#fff' }}>sunlight</b>, water and carbon dioxide — and packs that
          energy into <b style={{ color:G }}>glucose</b>. Everything else in the chapter builds on this one trade.</div>
        <div style={{ display:'inline-flex', alignItems:'center', gap:7, marginTop:13, padding:'8px 12px',
          borderRadius:10, background:'rgba(61,220,151,0.12)', border:'1px solid rgba(61,220,151,0.3)',
          color:G, fontWeight:600, fontSize:13 }}>
          <Icon name="quiz" size={15}/>Tap the leaf to see inside</div>
      </div>
      {/* nav */}
      <div style={{ display:'flex', alignItems:'center', gap:10, margin:'18px 20px 0' }}>
        <button style={{ flex:1, padding:'12px 0', borderRadius:12, background:'rgba(255,255,255,0.05)',
          border:'1px solid rgba(255,255,255,0.1)', color:'#9fe8c8', fontFamily:T.font, fontWeight:600, fontSize:14 }}>Back</button>
        <span style={{ fontFamily:T.mono, fontSize:12, color:'#6e9a86' }}>1 / 8</span>
        <button style={{ flex:1, padding:'12px 0', borderRadius:12, background:G, border:'none',
          color:'#08130d', fontFamily:T.font, fontWeight:700, fontSize:14, cursor:'pointer' }}>Next ›</button>
      </div>
    </div>
  );
}

Object.assign(window, { ProgressScreen, ViewerScreen, GeneratedScene, EventRow, ActiveCard, CompletedCard, RingProgress });
