/* ChapterStage — orchestrator (live Band simulation) + design canvas layout */
const { useState: useA, useRef: useRefA, useEffect: useEffA } = React;

const DEF_SETTINGS = { level:'Intermediate', style:'Visual Story', screens:8, brainstorm:true };

/* The scripted Band run. Each step reveals one trace event + status changes. */
const STEPS = [
  { d:600,  active:'coordinator', set:{coordinator:'active'},
    ev:{agent:'coordinator', type:'Delegated', title:'Briefed the Band', msg:'Routed the chapter to five specialists.', t:'00:01',
        payload:'job#PX-4471 · style=Visual Story · level=Intermediate'} },
  { d:1500, active:'structure', set:{coordinator:'completed', structure:'active'},
    ev:{agent:'structure', type:'Analyzed', title:'Mapping the chapter', msg:'Reading sections, figures and key terms…', t:'00:03'} },
  { d:1600, active:'structure', set:{},
    ev:{agent:'structure', type:'Generated', title:'Chapter map ready', msg:'6 core concepts, 3 sub-topics, 11 key terms.', t:'00:05',
        payload:'light reactions · Calvin cycle · chlorophyll · ATP …'} },
  { d:1500, active:'pedagogy', set:{structure:'completed', pedagogy:'active'},
    ev:{agent:'pedagogy', type:'Analyzed', title:'Finding confusions', msg:'Flagged 4 misconceptions learners commonly hold.', t:'00:07',
        payload:'“plants eat soil” · “photosynthesis = breathing”'} },
  { d:1500, active:'brainstorm', set:{pedagogy:'completed', brainstorm:'active'},
    ev:{agent:'brainstorm', type:'Brainstormed', title:'Five format concepts', msg:'Visual Story, Lecture, Map-first, Quiz-first, Case.', t:'00:09'} },
  { d:1400, active:'brainstorm', set:{},
    ev:{agent:'brainstorm', type:'Scored', title:'Scored concepts', msg:'Visual Story leads on clarity and engagement.', t:'00:11',
        payload:'visual 0.91 · case 0.78 · map 0.74 · quiz 0.70'} },
  { d:1100, active:'brainstorm', set:{},
    ev:{agent:'brainstorm', type:'Rejected', title:'Dropped Quiz-first', msg:'Too abstract before the core idea lands.', t:'00:12'} },
  { d:1000, active:'brainstorm', set:{},
    ev:{agent:'brainstorm', type:'Selected', title:'Selected Visual Story', msg:'Handing the blueprint to Visual Builder.', t:'00:13'} },
  { d:1600, active:'visual', set:{brainstorm:'completed', visual:'active'},
    ev:{agent:'visual', type:'Generated', title:'Building scenes', msg:'Composing interactive scenes with diagrams…', t:'00:15'} },
  { d:1800, active:'visual', set:{},
    ev:{agent:'visual', type:'Generated', title:'8 scenes built', msg:'Hook, chapter map, four concepts, quiz, recap.', t:'00:18',
        payload:'site 86 KB · no external scripts · 360px OK'} },
  { d:1500, active:'verifier', set:{visual:'completed', verifier:'active'},
    ev:{agent:'verifier', type:'Verified', title:'Faithfulness & safety', msg:'Cross-checked every claim against the source.', t:'00:20',
        payload:'faithfulness 0.96 · safety PASS'} },
  { d:1200, active:null, done:true, set:{verifier:'completed'},
    ev:{agent:'coordinator', type:'Published', title:'Experience published', msg:'Interactive chapter is live and shareable.', t:'00:22'} },
];
const CS_EVENTS = STEPS.map(s=>s.ev);
window.CS_EVENTS = CS_EVENTS;

const IDLE_SIM = { statuses:{}, activeId:null, events:[], progress:0, done:false, elapsed:0 };
const SNAP = {
  statuses:{ coordinator:'completed', structure:'completed', pedagogy:'completed', brainstorm:'active' },
  activeId:'brainstorm', events:CS_EVENTS.slice(0,7), progress:58, done:false, elapsed:12,
};
const DONE = {
  statuses:Object.fromEntries(AGENTS.map(a=>[a.id,'completed'])),
  activeId:null, events:CS_EVENTS, progress:100, done:true, elapsed:22,
};

/* ── Live orchestrator ─────────────────────────────────────── */
function ChapterStageApp() {
  const [screen,setScreen] = useA('splash');
  const [settings,setSettings] = useA(DEF_SETTINGS);
  const [sim,setSim] = useA(IDLE_SIM);
  const [viewer,setViewer] = useA('loading');
  const timers = useRefA([]);
  const interval = useRefA(null);

  const stop = ()=>{ timers.current.forEach(clearTimeout); timers.current=[];
    if(interval.current){ clearInterval(interval.current); interval.current=null; } };
  useEffA(()=>stop, []);

  const run = ()=>{
    stop();
    setSim({ ...IDLE_SIM });
    let statuses={}, events=[], elapsed=0;
    interval.current = setInterval(()=>{ elapsed+=1; setSim(s=> s.done ? s : { ...s, elapsed }); }, 1000);
    let acc=0;
    STEPS.forEach((step,idx)=>{
      acc += step.d;
      timers.current.push(setTimeout(()=>{
        statuses = { ...statuses, ...step.set };
        events = [ ...events, step.ev ];
        if (step.done && interval.current){ clearInterval(interval.current); interval.current=null; }
        setSim(s=>({
          statuses:{ ...statuses }, activeId: step.active!==undefined ? step.active : s.activeId,
          events:[ ...events ], progress: step.done?100:Math.round(((idx+1)/STEPS.length)*100),
          done: !!step.done, elapsed: step.done?Math.max(s.elapsed,22):s.elapsed,
        }));
      }, acc));
    });
  };

  const go = (s)=>{
    if (s==='progress'){ setScreen('progress'); run(); }
    else if (s==='viewer'){ setScreen('viewer'); setViewer('loading');
      timers.current.push(setTimeout(()=>setViewer('loaded'), 1700)); }
    else setScreen(s);
  };
  const patch = (p)=> setSettings(prev=>({ ...prev, ...p }));

  let el;
  if (screen==='splash')      el = <SplashScreen go={go}/>;
  else if (screen==='home')   el = <HomeScreen go={go}/>;
  else if (screen==='create') el = <CreateScreen go={go}/>;
  else if (screen==='settings') el = <SettingsScreen settings={settings} set={patch} go={go}/>;
  else if (screen==='progress') el = <ProgressScreen {...sim} layoutVariant="feed" stripVariant="chips" go={go} settings={settings}/>;
  else el = <ViewerScreen state={viewer} go={go} onRetry={()=>{ setViewer('loading'); timers.current.push(setTimeout(()=>setViewer('loaded'),1400)); }}/>;

  return <AndroidDevice dark>{el}</AndroidDevice>;
}

/* ── Static device wrapper ─────────────────────────────────── */
const noop = ()=>{};
function Phone({ children }) { return <AndroidDevice dark>{children}</AndroidDevice>; }

/* ── Canvas ────────────────────────────────────────────────── */
const W=412, H=892;
function App() {
  return (
    <DesignCanvas>
      <DCSection id="proto" title="Interactive Prototype" subtitle="Tap through the full flow — agents run live and auto-advance to a finished chapter. Open fullscreen for the best demo.">
        <DCArtboard id="live" label="Live · start → finish" width={W} height={H}><ChapterStageApp/></DCArtboard>
      </DCSection>

      <DCSection id="core" title="Core Screens" subtitle="The six surfaces of the control room.">
        <DCArtboard id="splash"   label="01 · Splash"            width={W} height={H}><Phone><SplashScreen go={noop}/></Phone></DCArtboard>
        <DCArtboard id="home"     label="02 · Home"              width={W} height={H}><Phone><HomeScreen go={noop}/></Phone></DCArtboard>
        <DCArtboard id="create"   label="03 · Create Chapter"    width={W} height={H}><Phone><CreateScreen go={noop}/></Phone></DCArtboard>
        <DCArtboard id="settings" label="04 · Generation Settings" width={W} height={H}><Phone><SettingsScreen settings={DEF_SETTINGS} set={noop} go={noop}/></Phone></DCArtboard>
        <DCArtboard id="progress" label="05 · Generation Progress" width={W} height={H}><Phone><ProgressScreen {...SNAP} layoutVariant="feed" stripVariant="chips" go={noop} settings={DEF_SETTINGS}/></Phone></DCArtboard>
        <DCArtboard id="viewer"   label="06 · Experience Viewer" width={W} height={H}><Phone><ViewerScreen state="loaded" go={noop}/></Phone></DCArtboard>
      </DCSection>

      <DCSection id="strip" title="Variation · Agent Strip" subtitle="How the Band is visualised on the Generation Progress screen — same moment, three treatments.">
        <DCArtboard id="v-chips" label="A · Hand-off chips"  width={W} height={H}><Phone><ProgressScreen {...SNAP} layoutVariant="feed" stripVariant="chips" go={noop} settings={DEF_SETTINGS}/></Phone></DCArtboard>
        <DCArtboard id="v-const" label="B · Constellation"   width={W} height={H}><Phone><ProgressScreen {...SNAP} layoutVariant="feed" stripVariant="constellation" go={noop} settings={DEF_SETTINGS}/></Phone></DCArtboard>
        <DCArtboard id="v-rail"  label="C · Stepper rail"    width={W} height={H}><Phone><ProgressScreen {...SNAP} layoutVariant="feed" stripVariant="rail" go={noop} settings={DEF_SETTINGS}/></Phone></DCArtboard>
      </DCSection>

      <DCSection id="layout" title="Variation · Progress Layout" subtitle="Two arrangements for the hero demo screen.">
        <DCArtboard id="l-feed"  label="A · Trace feed"      width={W} height={H}><Phone><ProgressScreen {...SNAP} layoutVariant="feed" stripVariant="chips" go={noop} settings={DEF_SETTINGS}/></Phone></DCArtboard>
        <DCArtboard id="l-focus" label="B · Band stage"      width={W} height={H}><Phone><ProgressScreen {...SNAP} layoutVariant="focus" go={noop} settings={DEF_SETTINGS}/></Phone></DCArtboard>
      </DCSection>

      <DCSection id="states" title="States" subtitle="Completed, loading and error states the developer needs to build.">
        <DCArtboard id="s-done"  label="Progress · Completed" width={W} height={H}><Phone><ProgressScreen {...DONE} layoutVariant="feed" stripVariant="chips" go={noop} settings={DEF_SETTINGS}/></Phone></DCArtboard>
        <DCArtboard id="s-load"  label="Viewer · Loading"     width={W} height={H}><Phone><ViewerScreen state="loading" go={noop}/></Phone></DCArtboard>
        <DCArtboard id="s-err"   label="Viewer · Error"       width={W} height={H}><Phone><ViewerScreen state="error" go={noop} onRetry={noop}/></Phone></DCArtboard>
      </DCSection>
    </DesignCanvas>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<App/>);
