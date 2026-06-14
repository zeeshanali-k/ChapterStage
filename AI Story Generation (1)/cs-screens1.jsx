/* ChapterStage — screens 1/2: Splash · Home · Create · Settings */
const { useState: useS1, useEffect: useE1 } = React;

/* ── shared inputs ─────────────────────────────────────────── */
function Field({ label, optional, children }) {
  return (
    <label style={{ display:'block' }}>
      <div style={{ display:'flex', alignItems:'center', gap:8, marginBottom:8 }}>
        <span style={{ fontSize:12.5, fontWeight:600, color:T.text2 }}>{label}</span>
        {optional && <span style={{ fontFamily:T.mono, fontSize:10, color:T.text3, letterSpacing:.5 }}>OPTIONAL</span>}
      </div>
      {children}
    </label>
  );
}
const inputStyle = {
  width:'100%', boxSizing:'border-box', background:'rgba(255,255,255,0.03)',
  border:`1px solid ${T.line}`, borderRadius:12, color:T.text, fontFamily:T.font,
  fontSize:15, padding:'13px 14px', outline:'none',
};
function TextInput(props){
  const [foc,setFoc]=useS1(false);
  return <input {...props} onFocus={()=>setFoc(true)} onBlur={()=>setFoc(false)}
    style={{ ...inputStyle, borderColor: foc?'rgba(124,92,255,0.6)':T.line,
      boxShadow: foc?'0 0 0 3px rgba(124,92,255,0.12)':'none', ...props.style }}/>;
}
function Seg({ options, value, onChange }) {
  const i = Math.max(0, options.findIndex(o=>(o.v||o)===value));
  return (
    <div style={{ position:'relative', display:'flex', background:'rgba(255,255,255,0.04)',
      border:`1px solid ${T.line}`, borderRadius:13, padding:4 }}>
      <div style={{ position:'absolute', top:4, bottom:4, left:`calc(${i*(100/options.length)}% + 4px)`,
        width:`calc(${100/options.length}% - 8px)`, background:T.primary, borderRadius:10,
        boxShadow:`0 4px 16px -4px ${T.primary}cc`, transition:'left .25s cubic-bezier(.4,0,.2,1)' }}/>
      {options.map(o=>{ const v=o.v||o, lab=o.label||o; const on=v===value;
        return <button key={v} onClick={()=>onChange(v)} style={{ flex:1, zIndex:1, padding:'10px 0',
          border:'none', background:'transparent', cursor:'pointer', fontFamily:T.font, fontWeight:600,
          fontSize:13.5, color:on?'#fff':T.text2, transition:'color .2s' }}>{lab}</button>; })}
    </div>
  );
}

/* ── 6.1 Splash ────────────────────────────────────────────── */
function SplashScreen({ go }) {
  return (
    <Screen pad={26} style={{ justifyContent:'center', alignItems:'center', gap:0,
      background:`radial-gradient(120% 50% at 50% 8%, #1A2138 0%, ${T.bg} 60%)` }}>
      <PlanetSystem/>

      <Logo size={26}/>
      <h1 style={{ fontFamily:T.display, fontWeight:600, fontSize:30, lineHeight:1.12, textAlign:'center',
        margin:'18px 0 0', letterSpacing:-0.5 }}>Books become<br/>interactive lessons</h1>
      <p style={{ color:T.text2, fontSize:14.5, textAlign:'center', lineHeight:1.5, margin:'12px 4px 0', textWrap:'pretty' }}>
        Powered by <span style={{ color:'#C9BBFF', fontWeight:600 }}>Band</span> multi-agent collaboration —
        a studio of specialists that build a chapter experience for you.</p>

      <div style={{ width:'100%', marginTop:26, display:'flex', flexDirection:'column', gap:11 }}>
        <Btn full size="lg" iconR="arrow" onClick={()=>go('home')}>Create Chapter Experience</Btn>
        <Btn full variant="ghost" icon="play" onClick={()=>go('home')}>View demo flow</Btn>
      </div>
    </Screen>
  );
}
function MiniConstellation() {
  const pts=[{x:0,y:-2,c:'#7C5CFF',s:30},{x:-46,y:18,c:'#22D3EE',s:22},{x:46,y:18,c:'#2EE59D',s:22},
    {x:-30,y:54,c:'#F6C85F',s:20},{x:30,y:54,c:'#FF8BD1',s:20}];
  return (
    <div style={{ position:'relative', width:0, height:0 }}>
      <svg width="140" height="100" style={{ position:'absolute', left:-70, top:-12 }}>
        {pts.slice(1).map((p,i)=><line key={i} x1="70" y1="16" x2={70+p.x} y2={16+p.y}
          stroke={p.c} strokeWidth="1" opacity="0.4"/>)}
      </svg>
      {pts.map((p,i)=>(
        <div key={i} style={{ position:'absolute', left:p.x, top:p.y, transform:'translate(-50%,-50%)',
          width:p.s, height:p.s, borderRadius:'50%', border:`1.5px solid ${p.c}`,
          background:`${p.c}22`, boxShadow:`0 0 14px ${p.c}66`,
          animation:`cs-float ${3+i*0.4}s ease-in-out ${i*0.2}s infinite` }}/>
      ))}
    </div>
  );
}

/* ── Planets (pure CSS spheres — render in every pipeline) ─── */
function Sphere({ d, bg, children, body }) {
  const s = body || d;
  return (
    <div style={{ position:'relative', width:s, height:s, borderRadius:'50%', overflow:'hidden',
      background:bg,
      boxShadow:`inset ${s*0.1}px ${s*0.1}px ${s*0.32}px rgba(255,255,255,0.28), `
        +`inset -${s*0.2}px -${s*0.18}px ${s*0.42}px rgba(0,0,0,0.6), 0 6px 16px rgba(0,0,0,0.45)` }}>
      {children}
    </div>
  );
}
function Ring({ d, w, h, rot, color, thick }) {
  return (
    <div style={{ position:'absolute', left:'50%', top:'50%', width:w, height:h, borderRadius:'50%',
      border:`${thick}px solid ${color}`, boxSizing:'border-box',
      transform:`translate(-50%,-50%) rotate(${rot}deg)` }}/>
  );
}
function Planet({ type, d }) {
  const wrap = { position:'relative', width:d, height:d, display:'flex', alignItems:'center', justifyContent:'center' };
  if (type==='earth') return (
    <div style={wrap}><Sphere d={d} bg="radial-gradient(circle at 34% 30%, #C8E7FF, #2E78E0 46%, #0E2E72 100%)">
      <div style={{ position:'absolute', width:d*0.46, height:d*0.32, left:d*0.08, top:d*0.42, background:'#3BA86A',
        borderRadius:'54% 46% 38% 62%', opacity:.92 }}/>
      <div style={{ position:'absolute', width:d*0.3, height:d*0.34, left:d*0.56, top:d*0.15, background:'#37A86A',
        borderRadius:'46% 54% 60% 40%', opacity:.85 }}/>
      <div style={{ position:'absolute', width:d*0.18, height:d*0.16, left:d*0.4, top:d*0.66, background:'#3BA86A',
        borderRadius:'50%', opacity:.8 }}/>
    </Sphere></div>
  );
  if (type==='jupiter') return (
    <div style={wrap}><Sphere d={d} bg="radial-gradient(circle at 35% 30%, #F0CFA6, #CB8552 50%, #7E4727 100%)">
      <div style={{ position:'absolute', inset:0, background:'linear-gradient(180deg,'
        +'rgba(168,98,58,0.0) 12%, rgba(168,98,58,0.5) 16%, rgba(240,210,170,0.35) 26%, rgba(168,98,58,0.0) 33%,'
        +'rgba(156,85,48,0.5) 46%, rgba(240,210,170,0.32) 58%, rgba(156,85,48,0.46) 70%, rgba(240,210,170,0.26) 82%)' }}/>
      <div style={{ position:'absolute', width:d*0.24, height:d*0.15, left:d*0.5, top:d*0.55, background:'#C5503B',
        borderRadius:'50%', boxShadow:'inset 0 0 4px rgba(0,0,0,0.4)' }}/>
    </Sphere></div>
  );
  if (type==='saturn') return (
    <div style={wrap}>
      <Ring d={d} w={d} h={d*0.4} rot={-20} color="#E9D199" thick={Math.max(2.5,d*0.05)}/>
      <Sphere d={d} body={d*0.6} bg="radial-gradient(circle at 38% 30%, #F8E7BC, #D9B265 55%, #8A6730 100%)"/>
      <div style={{ position:'absolute', left:'50%', top:'50%', width:d, height:d*0.4, borderRadius:'50%',
        borderBottom:`${Math.max(2.5,d*0.05)}px solid #EAD49E`, boxSizing:'border-box',
        transform:`translate(-50%,-50%) rotate(-20deg)`, clipPath:'inset(50% 0 0 0)' }}/>
    </div>
  );
  if (type==='uranus') return (
    <div style={wrap}>
      <Ring d={d} w={d*0.42} h={d} rot={10} color="#BDEFEF" thick={Math.max(2,d*0.045)}/>
      <Sphere d={d} body={d*0.82} bg="radial-gradient(circle at 40% 33%, #DCFAF6, #8FE0E2 52%, #3E97A0 100%)"/>
    </div>
  );
  /* neptune */
  return (
    <div style={wrap}><Sphere d={d} bg="radial-gradient(circle at 40% 32%, #8FB0FF, #3A63D8 48%, #162E7A 100%)">
      <div style={{ position:'absolute', inset:0, background:'linear-gradient(180deg,'
        +'rgba(175,198,255,0.28) 30%, transparent 40%, rgba(110,146,240,0.4) 58%, transparent 66%)' }}/>
      <div style={{ position:'absolute', width:d*0.16, height:d*0.12, left:d*0.34, top:d*0.42, background:'#172C6E',
        borderRadius:'50%', opacity:.55 }}/>
    </Sphere></div>
  );
}

const PLANETS = [
  { type:'saturn',  d:62, r:20, x:150, y:36,  c:'#F6C85F' },
  { type:'earth',   d:42, r:21, x:262, y:94,  c:'#22D3EE' },
  { type:'neptune', d:38, r:19, x:219, y:188, c:'#FF5C7A' },
  { type:'jupiter', d:60, r:30, x:81,  y:188, c:'#FF8BD1' },
  { type:'uranus',  d:40, r:18, x:38,  y:94,  c:'#2EE59D' },
];
function PlanetSystem() {
  const cx=150, cy=120;
  return (
    <div style={{ position:'relative', width:300, height:244, marginBottom:4 }}>
      <div style={{ position:'absolute', left:cx, top:cy, transform:'translate(-50%,-50%)',
        width:200, height:200, borderRadius:'50%', background:'radial-gradient(circle, rgba(124,92,255,0.18), transparent 68%)' }}/>
      <svg width="300" height="244" style={{ position:'absolute', inset:0 }}>
        {/* orbit rings */}
        <ellipse cx={cx} cy={cy} rx="118" ry="84" fill="none" stroke="rgba(255,255,255,0.08)" strokeWidth="1"/>
        <ellipse cx={cx} cy={cy} rx="80" ry="56" fill="none" stroke="rgba(255,255,255,0.05)" strokeWidth="1"/>
        {/* stars */}
        {[[24,30],[276,40],[40,200],[262,210],[150,18],[210,118],[96,232],[286,150],[18,120]].map((s,i)=>(
          <circle key={i} cx={s[0]} cy={s[1]} r={i%3===0?1.4:0.9} fill="#fff" opacity={0.15+(i%4)*0.12}/>
        ))}
        {/* agent-tinted tethers — core edge → planet edge */}
        {PLANETS.map(p=>{
          const dx=p.x-cx, dy=p.y-cy, len=Math.hypot(dx,dy), ux=dx/len, uy=dy/len;
          return <line key={p.type} x1={cx+ux*28} y1={cy+uy*28} x2={p.x-ux*(p.r+3)} y2={p.y-uy*(p.r+3)}
            stroke={p.c} strokeWidth="1.3" strokeDasharray="2 4" opacity=".4"/>;
        })}
      </svg>
      {/* central chapter core */}
      <div style={{ position:'absolute', left:cx, top:cy, transform:'translate(-50%,-50%)',
        width:52, height:52, borderRadius:16, display:'flex', alignItems:'center', justifyContent:'center',
        background:'linear-gradient(150deg, #8B6BFF, #5B7BFF)', color:'#fff',
        boxShadow:'0 0 34px -4px rgba(124,92,255,0.85), inset 0 1px 0 rgba(255,255,255,0.4)',
        border:'1px solid rgba(255,255,255,0.3)' }}>
        <Icon name="book" size={24}/>
        <span style={{ position:'absolute', inset:-6, borderRadius:20, border:'1.4px solid rgba(124,92,255,0.5)',
          animation:'cs-ring 2.6s ease-out infinite' }}/>
      </div>
      {/* planets */}
      {PLANETS.map(p=>(
        <div key={p.type} style={{ position:'absolute', left:p.x, top:p.y, transform:'translate(-50%,-50%)' }}>
          <Planet type={p.type} d={p.d}/>
        </div>
      ))}
    </div>
  );
}

/* ── 6.2 Home ──────────────────────────────────────────────── */
const HOW = [
  { icon:'upload', t:'Upload a chapter', s:'Paste text or drop a PDF / TXT', c:'#22D3EE' },
  { icon:'route',  t:'Agents collaborate', s:'Six specialists analyze, build & verify', c:'#7C5CFF' },
  { icon:'external', t:'Open interactive link', s:'A polished web experience, ready to share', c:'#2EE59D' },
];
const JOBS = [
  { title:'Photosynthesis', book:'Living Systems · Ch.4', status:'ready', style:'Visual Story', when:'2m ago' },
  { title:'Supply & Demand', book:'Foundations of Economics · Ch.2', status:'generating', style:'Case Study', when:'now' },
  { title:'The French Revolution', book:'A Short History · Ch.9', status:'ready', style:'Concept Map', when:'1h ago' },
];
function HomeScreen({ go }) {
  return (
    <Screen>
      <TopBar brand right={
        <div style={{ width:34, height:34, borderRadius:'50%', border:`1px solid ${T.line}`,
          background:'rgba(255,255,255,0.04)', display:'flex', alignItems:'center', justifyContent:'center' }}>
          <span style={{ fontFamily:T.mono, fontWeight:700, fontSize:13, color:T.text2 }}>AK</span></div>}/>

      <div className="cs-up" style={{ marginTop:4 }}>
        <div style={{ fontSize:13, color:T.text2 }}>Good evening, Aria</div>
        <h2 style={{ fontFamily:T.display, fontWeight:600, fontSize:25, margin:'2px 0 0', letterSpacing:-0.4 }}>
          Let’s stage a chapter.</h2>
      </div>

      {/* hero CTA */}
      <Card pad={0} glow={T.primary} style={{ overflow:'hidden', cursor:'pointer' }} onClick={()=>go('create')}>
        <div style={{ position:'relative', padding:20,
          background:'radial-gradient(120% 120% at 100% 0%, rgba(124,92,255,0.28), transparent 60%)' }}>
          <div style={{ position:'absolute', right:-10, top:-10, opacity:0.5 }}><MiniConstellation/></div>
          <Label dot={T.primary}>NEW EXPERIENCE</Label>
          <div style={{ fontFamily:T.display, fontWeight:600, fontSize:21, margin:'12px 0 4px', maxWidth:200 }}>
            Create a new chapter experience</div>
          <div style={{ fontSize:13, color:T.text2, maxWidth:210, lineHeight:1.45 }}>
            Turn dense chapters into visual learning in one link.</div>
          <Btn size="md" iconR="arrow" style={{ marginTop:16 }}>Start</Btn>
        </div>
      </Card>

      {/* how it works */}
      <div>
        <Label style={{ marginBottom:11 }}>HOW IT WORKS</Label>
        <div style={{ display:'flex', flexDirection:'column', gap:9 }}>
          {HOW.map((h,i)=>(
            <Card key={i} pad={14} style={{ display:'flex', alignItems:'center', gap:13 }}>
              <div style={{ width:40, height:40, borderRadius:11, flexShrink:0, display:'flex',
                alignItems:'center', justifyContent:'center', color:h.c, background:`${h.c}16`,
                border:`1px solid ${h.c}33` }}><Icon name={h.icon} size={19}/></div>
              <div style={{ flex:1 }}>
                <div style={{ fontWeight:600, fontSize:14.5 }}>{h.t}</div>
                <div style={{ fontSize:12.5, color:T.text2, marginTop:1 }}>{h.s}</div>
              </div>
              <span style={{ fontFamily:T.mono, fontSize:11, color:T.text3 }}>0{i+1}</span>
            </Card>
          ))}
        </div>
      </div>

      {/* recent jobs */}
      <div>
        <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:11 }}>
          <Label>RECENT JOBS</Label>
          <span style={{ fontSize:12, color:T.text3 }}>3 total</span>
        </div>
        <div style={{ display:'flex', flexDirection:'column', gap:9 }}>
          {JOBS.map((j,i)=><RecentJob key={i} job={j} onClick={()=>go(j.status==='generating'?'progress':'viewer')}/>)}
        </div>
      </div>
    </Screen>
  );
}
function RecentJob({ job, onClick }) {
  const ready = job.status==='ready';
  return (
    <Card pad={14} onClick={onClick} style={{ cursor:'pointer', display:'flex', alignItems:'center', gap:13 }}>
      <div style={{ width:42, height:42, borderRadius:11, flexShrink:0, background:'rgba(255,255,255,0.04)',
        border:`1px solid ${T.line}`, display:'flex', alignItems:'center', justifyContent:'center', color:T.text2 }}>
        <Icon name="book" size={19}/></div>
      <div style={{ flex:1, minWidth:0 }}>
        <div style={{ fontWeight:600, fontSize:14.5, whiteSpace:'nowrap', overflow:'hidden', textOverflow:'ellipsis' }}>{job.title}</div>
        <div style={{ fontSize:11.5, color:T.text3, fontFamily:T.mono, marginTop:2 }}>{job.book}</div>
      </div>
      <div style={{ textAlign:'right', flexShrink:0 }}>
        <span style={{ display:'inline-flex', alignItems:'center', gap:6, padding:'4px 9px', borderRadius:8,
          fontSize:11, fontWeight:600, fontFamily:T.mono, letterSpacing:.3,
          background: ready?'rgba(46,229,157,0.12)':'rgba(246,200,95,0.12)',
          color: ready?T.success:T.warn }}>
          <StatusDot status={ready?'completed':'active'} color={T.warn} size={6}/>
          {ready?'READY':'BUILDING'}</span>
        <div style={{ fontSize:11, color:T.text3, marginTop:5 }}>{job.when}</div>
      </div>
    </Card>
  );
}

/* ── 6.3 Create ────────────────────────────────────────────── */
const SAMPLE_TEXT = `Photosynthesis is the process by which green plants, algae, and some bacteria convert light energy into chemical energy stored in glucose. It takes place mainly in the chloroplasts, where the pigment chlorophyll absorbs light — most strongly in the blue and red wavelengths.

The process has two linked stages. In the light-dependent reactions, water is split and light energy is captured as ATP and NADPH. In the Calvin cycle, that energy is used to fix carbon dioxide into sugar...`;
function CreateScreen({ go }) {
  const [mode,setMode]=useS1('paste');
  const [text,setText]=useS1('');
  const [file,setFile]=useS1(null);
  const ready = mode==='paste' ? text.trim().length>40 : !!file;
  return (
    <Screen>
      <TopBar title="Create chapter" sub="Step 1 of 2 · Source content"
        left={<BackBtn onClick={()=>go('home')}/>}/>
      <Seg options={[{v:'paste',label:'Paste text'},{v:'upload',label:'Upload file'}]} value={mode} onChange={setMode}/>

      <div style={{ display:'flex', gap:12 }}>
        <div style={{ flex:1 }}><Field label="Book title" optional>
          <TextInput placeholder="Living Systems"/></Field></div>
      </div>
      <Field label="Chapter title" optional>
        <TextInput placeholder="Ch. 4 · Photosynthesis"/></Field>

      {mode==='paste' ? (
        <Field label="Chapter text">
          <div style={{ position:'relative' }}>
            <textarea value={text} onChange={e=>setText(e.target.value)}
              placeholder="Paste your chapter here…"
              style={{ ...inputStyle, minHeight:172, resize:'none', lineHeight:1.5, fontSize:14 }}/>
            {!text && <button onClick={()=>setText(SAMPLE_TEXT)} style={{ position:'absolute', right:10, bottom:10,
              display:'inline-flex', alignItems:'center', gap:6, padding:'7px 11px', borderRadius:9,
              background:'rgba(124,92,255,0.14)', border:'1px solid rgba(124,92,255,0.3)', color:'#C9BBFF',
              fontFamily:T.font, fontWeight:600, fontSize:12, cursor:'pointer' }}>
              <Icon name="spark" size={14}/>Use sample</button>}
          </div>
          <Helper>{text? `${text.trim().split(/\s+/).length} words · ready to stage` : 'Paste at least a few paragraphs for the best results.'}</Helper>
        </Field>
      ) : (
        <Field label="Upload file">
          {!file ? (
            <div onClick={()=>setFile({name:'photosynthesis-ch4.pdf', size:'248 KB'})} style={{ cursor:'pointer',
              border:`1.5px dashed ${T.lineHi}`, borderRadius:14, padding:'30px 18px', textAlign:'center',
              background:'rgba(255,255,255,0.02)' }}>
              <div style={{ width:50, height:50, margin:'0 auto 12px', borderRadius:14, background:T.primaryDim,
                border:'1px solid rgba(124,92,255,0.3)', display:'flex', alignItems:'center', justifyContent:'center', color:'#C9BBFF' }}>
                <Icon name="upload" size={23}/></div>
              <div style={{ fontWeight:600, fontSize:15 }}>Drop a file or tap to browse</div>
              <div style={{ fontSize:12.5, color:T.text2, marginTop:4 }}>We’ll extract the text for you</div>
              <div style={{ display:'inline-flex', gap:6, marginTop:12 }}>
                {['PDF','TXT'].map(t=><span key={t} style={{ fontFamily:T.mono, fontSize:10.5, letterSpacing:.5,
                  padding:'4px 9px', borderRadius:7, background:'rgba(255,255,255,0.05)', color:T.text2,
                  border:`1px solid ${T.line}` }}>{t}</span>)}
              </div>
            </div>
          ) : (
            <Card pad={14} style={{ display:'flex', alignItems:'center', gap:12 }}>
              <div style={{ width:40, height:40, borderRadius:10, background:'rgba(255,92,122,0.12)',
                color:'#FF8FA6', display:'flex', alignItems:'center', justifyContent:'center', flexShrink:0 }}>
                <Icon name="doc" size={20}/></div>
              <div style={{ flex:1, minWidth:0 }}>
                <div style={{ fontWeight:600, fontSize:14, whiteSpace:'nowrap', overflow:'hidden', textOverflow:'ellipsis' }}>{file.name}</div>
                <div style={{ fontSize:11.5, color:T.text3, fontFamily:T.mono }}>{file.size} · extracted</div>
              </div>
              <button onClick={()=>setFile(null)} style={{ background:'none', border:'none', color:T.text3,
                cursor:'pointer', padding:6, transform:'rotate(45deg)' }}><Icon name="plus" size={20}/></button>
            </Card>
          )}
          <Helper>Accepted: PDF or TXT, up to 20 MB. No login or personal data needed.</Helper>
        </Field>
      )}

      <div style={{ flex:1 }}/>
      <Btn full size="lg" iconR="arrow" onClick={()=>go('settings')}
        style={{ opacity:ready?1:0.45, pointerEvents:ready?'auto':'none', position:'sticky', bottom:0 }}>Continue</Btn>
    </Screen>
  );
}
function Helper({ children }) {
  return <div style={{ fontSize:12, color:T.text3, marginTop:8, lineHeight:1.45 }}>{children}</div>;
}
function BackBtn({ onClick }) {
  return <button onClick={onClick} style={{ width:38, height:38, borderRadius:11, flexShrink:0,
    background:'rgba(255,255,255,0.04)', border:`1px solid ${T.line}`, color:T.text, cursor:'pointer',
    display:'flex', alignItems:'center', justifyContent:'center', transform:'scaleX(-1)' }}>
    <Icon name="arrow" size={18}/></button>;
}

/* ── 6.4 Generation Settings ───────────────────────────────── */
const STYLES = [
  { v:'Visual Story', icon:'layers', desc:'Cinematic scenes that build the concept step by step.' },
  { v:'Lecture Mode', icon:'chat', desc:'Structured explainer with clear sections and notes.' },
  { v:'Concept Map First', icon:'map', desc:'Lead with a map of how the ideas connect.' },
  { v:'Quiz First', icon:'quiz', desc:'Probe understanding, then teach to the gaps.' },
  { v:'Case Study', icon:'target', desc:'Anchor the chapter in one concrete real example.' },
];
function SettingsScreen({ go, settings, set }) {
  const cur = STYLES.find(s=>s.v===settings.style) || STYLES[0];
  return (
    <Screen>
      <TopBar title="Generation settings" sub="Step 2 of 2 · Shape the experience"
        left={<BackBtn onClick={()=>go('create')}/>}/>

      {/* live preview */}
      <Card pad={0} glow={T.primary} style={{ overflow:'hidden' }}>
        <div style={{ padding:16, background:'radial-gradient(120% 120% at 0% 0%, rgba(124,92,255,0.22), transparent 55%)' }}>
          <div style={{ display:'flex', alignItems:'center', gap:11, marginBottom:14 }}>
            <div style={{ width:44, height:44, borderRadius:12, background:T.primaryDim, color:'#C9BBFF',
              border:'1px solid rgba(124,92,255,0.32)', display:'flex', alignItems:'center', justifyContent:'center' }}>
              <Icon name={cur.icon} size={22}/></div>
            <div style={{ flex:1 }}>
              <Label dot={T.primary}>PREVIEW</Label>
              <div style={{ fontFamily:T.display, fontWeight:600, fontSize:17, marginTop:3 }}>{cur.v}</div>
            </div>
          </div>
          <div style={{ fontSize:13, color:T.text2, lineHeight:1.5 }}>{cur.desc}</div>
          <div style={{ display:'flex', gap:6, marginTop:13 }}>
            {Array.from({length:settings.screens}).map((_,i)=>(
              <div key={i} style={{ flex:1, height:30, borderRadius:6,
                background: i===0?'rgba(124,92,255,0.4)':'rgba(255,255,255,0.06)',
                border:`1px solid ${i===0?'rgba(124,92,255,0.5)':T.line}` }}/>
            ))}
          </div>
          <div style={{ fontFamily:T.mono, fontSize:10.5, color:T.text3, marginTop:7, letterSpacing:.5 }}>
            {settings.screens} SCENES · {settings.level.toUpperCase()}{settings.brainstorm?' · AUTO-BRAINSTORM':''}</div>
        </div>
      </Card>

      <SettingBlock label="AUDIENCE LEVEL">
        <PillGroup options={['Beginner','Intermediate','Expert']} value={settings.level}
          onChange={v=>set({level:v})}/>
      </SettingBlock>

      <SettingBlock label="EXPERIENCE STYLE">
        <PillGroup options={STYLES.map(s=>s.v)} value={settings.style} onChange={v=>set({style:v})}/>
      </SettingBlock>

      <SettingBlock label="SCREEN COUNT">
        <Seg options={[{v:6,label:'6'},{v:8,label:'8'},{v:10,label:'10'}]} value={settings.screens}
          onChange={v=>set({screens:v})}/>
      </SettingBlock>

      <Card pad={15} style={{ display:'flex', alignItems:'center', gap:13 }}>
        <div style={{ width:40, height:40, borderRadius:11, flexShrink:0, color:T.warn,
          background:'rgba(246,200,95,0.12)', border:'1px solid rgba(246,200,95,0.3)',
          display:'flex', alignItems:'center', justifyContent:'center' }}><Icon name="spark" size={19}/></div>
        <div style={{ flex:1 }}>
          <div style={{ fontWeight:600, fontSize:14.5 }}>Auto-Brainstorm</div>
          <div style={{ fontSize:12.5, color:T.text2, marginTop:1, lineHeight:1.4 }}>
            Let agents test 5 formats and pick the best.</div>
        </div>
        <Toggle on={settings.brainstorm} onChange={v=>set({brainstorm:v})}/>
      </Card>

      <div style={{ flex:1, minHeight:6 }}/>
      <Btn full size="lg" icon="route" onClick={()=>go('progress')} style={{ position:'sticky', bottom:0 }}>
        Start Agent Workflow</Btn>
    </Screen>
  );
}
function SettingBlock({ label, children }) {
  return <div><Label style={{ marginBottom:11 }}>{label}</Label>{children}</div>;
}

Object.assign(window, { SplashScreen, HomeScreen, CreateScreen, SettingsScreen, BackBtn, Helper });
