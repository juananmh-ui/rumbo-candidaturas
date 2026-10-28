/* Rumbo — Oscar Ramírez Toro y Juan Antonio Moreno Hevilla. */
(function(root,factory){ const api=factory(); if(typeof module==='object'&&module.exports)module.exports=api; else root.RumboModel=api; })(typeof window==='object'?window:globalThis,function(){
  'use strict';
  const STATES=['pendiente','entrevista','rechazado','aceptado'];
  const LABELS={pendiente:'Pendiente',entrevista:'Entrevista',rechazado:'Rechazado',aceptado:'Aceptado'};
  const HEADER='id;titulo;empresa;estado;fechaPostulacion;enlace;notas';
  const normalize=s=>String(s??'').normalize('NFD').replace(/[\u0300-\u036f]/g,'').toLocaleLowerCase('es').trim();
  const validISO=s=>{if(!/^\d{4}-\d{2}-\d{2}$/.test(s))return false; const [y,m,d]=s.split('-').map(Number); if(y<1)return false; const dt=new Date(0);dt.setUTCHours(0,0,0,0);dt.setUTCFullYear(y,m-1,d);return dt.getUTCFullYear()===y&&dt.getUTCMonth()===m-1&&dt.getUTCDate()===d;};
  const displayDate=s=>validISO(s)?s.slice(8)+'/'+s.slice(5,7)+'/'+s.slice(0,4):s;
  const fromDate=s=>{const m=/^(\d{2})\/(\d{2})\/(\d{4})$/.exec(s);if(!m)throw Error('Usa fechas con formato dd/MM/yyyy.');const iso=m[3]+'-'+m[2]+'-'+m[1];if(!validISO(iso))throw Error('La fecha '+s+' no existe.');return iso;};
  const safeUrl=value=>{if(!String(value??'').trim())return '';try{const u=new URL(String(value).trim());if(['http:','https:'].includes(u.protocol))return u.href;}catch{}return null;};
  function validateJob(raw){
    const value={titulo:String(raw.titulo??'').trim(),empresa:String(raw.empresa??'').trim(),estado:String(raw.estado??''),fecha:String(raw.fecha??''),enlace:String(raw.enlace??'').trim(),notas:String(raw.notas??'').trim()};
    const errors={};
    if(!value.titulo)errors.titulo='Escribe el título del puesto.';
    if(!value.empresa)errors.empresa='Indica la empresa.';
    if(!STATES.includes(value.estado))errors.estado='Selecciona un estado válido.';
    if(!validISO(value.fecha))errors.fecha='Indica una fecha real.';
    if(safeUrl(value.enlace)===null)errors.enlace='Utiliza un enlace que empiece por https:// o http://.';
    for(const key of ['titulo','empresa','enlace'])if(/[\r\n\0]/.test(value[key]))errors[key]='Este campo debe ocupar una sola línea.';
    if(/[\r\n\0]/.test(value.notas))errors.notas='Escribe la nota en una sola línea para conservar el formato CSV de v1.0.';
    if(value.titulo.length>160)errors.titulo='Máximo 160 caracteres.';
    if(value.empresa.length>160)errors.empresa='Máximo 160 caracteres.';
    if(value.notas.length>5000)errors.notas='Máximo 5000 caracteres.';
    return {value,errors};
  }
  function filterJobs(jobs,filters={}){return jobs.filter(j=>(!filters.estado||j.estado===filters.estado)&&(!filters.empresa||normalize(j.empresa).includes(normalize(filters.empresa)))).slice().sort((a,b)=>b.id-a.id);}
  function saveJob(jobs,raw,id=null){const checked=validateJob(raw);if(Object.keys(checked.errors).length)return {errors:checked.errors};if(id!==null&&!jobs.some(j=>j.id===id))throw Error('La candidatura ya no existe.'); const next=id??Math.max(0,...jobs.map(j=>j.id))+1;if(!Number.isSafeInteger(next))throw Error('No se pueden asignar más identificadores.');const job={id:next,...checked.value};return {job,jobs:id===null?[...jobs,job]:jobs.map(j=>j.id===id?job:j)};}
  function deleteJob(jobs,id){if(!jobs.some(j=>j.id===id))throw Error('La candidatura ya no existe.');return jobs.filter(j=>j.id!==id);}
  function splitLine(line){const result=[];let value='',quoted=false,closed=false;for(let i=0;i<line.length;i++){const c=line[i];if(quoted){if(c==='"'){if(line[i+1]==='"'){value+='"';i++;}else{quoted=false;closed=true;}}else value+=c;}else if(c===';'){result.push(value);value='';closed=false;}else if(c==='"'&&!value&&!closed)quoted=true;else{if(c==='"'||closed)throw Error('Comillas CSV mal colocadas.');value+=c;}}if(quoted)throw Error('Comillas CSV sin cerrar.');result.push(value);return result;}
  function parseCsv(text){const lines=text.replace(/^\uFEFF/,'').split(/\r?\n/),jobs=[],ids=new Set();for(let i=0;i<lines.length;i++){if(!lines[i].trim())continue;try{if(!jobs.length&&lines[i].trim()===HEADER)continue;const c=splitLine(lines[i]);if(c.length!==7)throw Error('Se esperaban 7 campos separados por punto y coma.');const id=Number(c[0]);if(!/^[1-9]\d*$/.test(c[0].trim())||!Number.isSafeInteger(id))throw Error('El ID debe ser un entero positivo.');if(ids.has(id))throw Error('El ID '+id+' está repetido.');const checked=validateJob({titulo:c[1],empresa:c[2],estado:normalize(c[3]),fecha:fromDate(c[4].trim()),enlace:c[5],notas:c[6]});if(Object.keys(checked.errors).length)throw Error(Object.values(checked.errors)[0]);jobs.push({id,...checked.value});ids.add(id);}catch(e){throw Error('Línea '+(i+1)+': '+e.message);}}if(!jobs.length)throw Error('El archivo no contiene candidaturas.');return jobs;}
  function importJobs(existing,text){const incoming=parseCsv(text),ids=new Set(existing.map(j=>j.id));for(const j of incoming)if(ids.has(j.id))throw Error('El ID '+j.id+' ya existe. No se ha importado ningún registro.');return [...existing,...incoming];}
  const quote=s=>/[;"\r\n]/.test(String(s))?'"'+String(s).replaceAll('"','""')+'"':String(s);
  const exportCsv=jobs=>HEADER+'\r\n'+jobs.slice().sort((a,b)=>a.id-b.id).map(j=>[j.id,j.titulo,j.empresa,j.estado,displayDate(j.fecha),j.enlace,j.notas].map(quote).join(';')).join('\r\n')+'\r\n';
  return {STATES,LABELS,HEADER,normalize,validISO,displayDate,fromDate,safeUrl,validateJob,filterJobs,saveJob,deleteJob,parseCsv,importJobs,exportCsv};
});
