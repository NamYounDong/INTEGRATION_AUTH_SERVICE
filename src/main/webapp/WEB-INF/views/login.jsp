<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width,initial-scale=1"/>
  <title>로그인 | Google OAuth</title>

  <!-- 기본 다크 모던 테마 -->
  <style>
    :root{
      --bg:#0f172a; --card:#111827; --muted:#94a3b8; --text:#e5e7eb;
      --accent:#22c55e; --accent-2:#60a5fa; --ring:#334155; --input:#1f2937; --hover:#374151;
    }
    *{box-sizing:border-box} html,body{height:100%}
    body{
      margin:0; background:radial-gradient(1200px 800px at 85% -10%, rgba(96,165,250,.15), transparent),
               radial-gradient(1000px 600px at -10% 110%, rgba(34,197,94,.12), transparent), var(--bg);
      color:var(--text); font-family:ui-sans-serif,system-ui,-apple-system,Segoe UI,Roboto,Helvetica,Arial;
      display:flex; align-items:center; justify-content:center; padding:24px;
    }
    .card{
      width:100%; max-width:560px;
      background:linear-gradient(180deg, rgba(255,255,255,.04), rgba(255,255,255,.02));
      border:1px solid rgba(255,255,255,.06); border-radius:20px; backdrop-filter: blur(10px);
      box-shadow: 0 10px 30px rgba(0,0,0,.35); overflow:hidden;
    }
    .card-header{ padding:28px 28px 12px; border-bottom:1px solid rgba(255,255,255,.06); }
    .badge{ display:inline-flex; align-items:center; gap:8px; font-size:12px; color:#cbd5e1; letter-spacing:.3px;
      background: rgba(96,165,250,.15); padding:6px 10px; border-radius:999px; border:1px solid rgba(96,165,250,.25);}
    .title{ margin:16px 0 6px; font-size:22px; font-weight:700; letter-spacing:.2px;}
    .subtitle{ margin:0; color:var(--muted); font-size:14px; line-height:1.5;}
    .card-body{ padding:24px 28px 28px; display:grid; gap:16px;}
    label{font-size:13px; color:#cbd5e1; margin-bottom:6px; display:block}
    .hint{ font-size:12px; color:var(--muted); margin-top:4px;}
    .error{ color:#fda4af; font-size:12px; margin-top:-2px; display:none;}

    .preview{
      background:#0b1220; border:1px dashed #334155; border-radius:12px; padding:12px;
      font-family:ui-monospace,SFMono-Regular,Menlo,Consolas,"Liberation Mono","Courier New",monospace;
      color:#cbd5e1; font-size:12px; overflow:auto;
    }
    .btn{
      width:100%; display:inline-flex; justify-content:center; align-items:center; gap:10px;
      background:linear-gradient(180deg, #1f2937, #111827); border:1px solid #273244; color:#e5e7eb;
      padding:12px 16px; border-radius:12px; font-weight:600; letter-spacing:.2px; cursor:pointer;
      transition: transform .05s ease, background-color .2s, border-color .2s, box-shadow .2s;
    }
    .btn:hover{ background:#1f2937; border-color:#3b82f6; box-shadow:0 6px 14px rgba(59,130,246,.15); }
    .btn:active{ transform:translateY(1px); }
    .btn .g{ width:18px; height:18px; }
    .divider{ display:flex; align-items:center; gap:10px; color:#94a3b8; font-size:12px; }
    .divider::before, .divider::after{ content:""; height:1px; flex:1; background:linear-gradient(90deg, transparent, #334155, transparent); }
    .footer{ padding:16px 28px 24px; color:#64748b; font-size:12px; display:flex; justify-content:space-between; align-items:center;}
    .link{ color:#93c5fd; text-decoration:none; border-bottom:1px dashed rgba(147,197,253,.5); }
    .link:hover{ color:#bfdbfe; }
  </style>

  <!-- Tom Select (커스텀 셀렉트) -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/tom-select@2.3.1/dist/css/tom-select.css"/>
  <style>
    /* Tom Select 다크 커스텀 */
    .ts-wrapper.single .ts-control, .ts-wrapper .ts-control{
      background: var(--input); border:1px solid var(--ring); border-radius:12px; padding:10px 12px;
      color:var(--text); min-height:44px; box-shadow:none;
    }
    .ts-wrapper.focus .ts-control{ border-color:var(--accent-2); box-shadow:0 0 0 4px rgba(96,165,250,.15); }
    .ts-dropdown{
      background:#0b1220; border:1px solid #273244; border-radius:12px; box-shadow:0 16px 40px rgba(0,0,0,.35);
      margin-top:8px; overflow:hidden;
    }
    .ts-dropdown .option, .ts-dropdown .create{ padding:10px 12px; border-bottom:1px dashed rgba(255,255,255,.05); }
    .ts-dropdown .option:last-child{ border-bottom:0; }
    .ts-dropdown .active{ background:rgba(96,165,250,.12); }
    .svc-title{ font-weight:700; color:#e5e7eb; font-size:14px; letter-spacing:.2px; display:block; }
    .svc-sub{ color:#94a3b8; font-size:12px; display:block; margin-top:2px; word-break:break-all; }
    .ts-control .item{ display:flex; flex-direction:column; gap:2px; padding:2px 0; }
    .ts-control .item .svc-title{ font-size:13px; }
    .ts-control .item .svc-sub{ font-size:11px; color:#9aa7b9; }
    .ts-dropdown .ts-input{ background:#0b1220; border-bottom:1px solid #273244; padding:10px 12px; }
  </style>
</head>
<body>
  <div class="card" role="region" aria-labelledby="loginTitle">
    <div class="card-header">
      <span class="badge">🔐 Google OAuth</span>
      <h1 id="loginTitle" class="title">서비스 선택 후 Google로 로그인</h1>
      <p class="subtitle">로그인 완료 후 선택한 서비스의 Callback URL로 리다이렉트됩니다.</p>
    </div>

    <div class="card-body">
      <!-- 서비스 선택 -->
      <div>
        <label for="svcSelect">서비스 선택</label>
        <!-- 서버가 내려주는 select를 Tom Select가 래핑합니다 -->
        <select id="svcSelect" aria-describedby="svcHint" placeholder="서비스를 검색 또는 선택하세요">
          <option value="" data-clbck="">— 서비스를 선택하세요 —</option>
          <c:forEach var="svc" items="${srvcList}">
            <option value="${svc.srvcSeq}" data-name="${svc.srvcNm}" data-clbck="${svc.clbckUrl}">
              ${svc.srvcNm}
            </option>
          </c:forEach>
        </select>
        <div id="svcHint" class="hint">서비스명 또는 URL로 검색할 수 있습니다.</div>
        <div id="svcError" class="error">서비스를 먼저 선택해주세요.</div>
      </div>

      <!-- 선택 미리보기 -->
      <div>
        <label>선택된 서비스 정보</label>
        <div class="preview" id="previewBox">
          { "srvcSeq": null, "srvcNm": null, "clbckUrl": null }
        </div>
      </div>

      <div class="divider">계속하려면</div>

      <!-- 구글 OAuth 시작 -->
      <button type="button" class="btn" id="googleBtn" aria-label="Google로 로그인">
        <svg class="g" viewBox="0 0 533.5 544.3" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
          <path d="M533.5 278.4c0-18.6-1.6-37.1-5-55H272v104.2h147.3c-6.3 33.9-25.7 62.6-54.7 81.8v67h88.5c51.8-47.7 80.4-118.1 80.4-198z" fill="#4285F4"/>
          <path d="M272 544.3c73.8 0 135.7-24.4 181-66.1l-88.5-67c-24.6 16.5-56.1 26.3-92.5 26.3-71 0-131.2-47.9-152.8-112.2h-91.6v70.3c45.4 90 138.3 148.7 244.4 148.7z" fill="#34A853"/>
          <path d="M119.2 325.3c-10.1-29.9-10.1-62.7 0-92.6v-70.3H27.6c-40.5 80.8-40.5 175.1 0 255.8l91.6-70.3z" fill="#FBBC05"/>
          <path d="M272 107.7c38.5-.6 75.6 13.7 103.9 39.8l77.6-77.6C407.5 24.2 343.3.1 272 0 165.9 0 73 58.7 27.6 148.7l91.6 70.3C140.8 154.7 201 107.7 272 107.7z" fill="#EA4335"/>
        </svg>
        Google로 계속
      </button>
    </div>

    <div class="footer">
      <span>© <script>document.write(new Date().getFullYear())</script> DocMangler</span>
      <a class="link" href="/pg/home">홈으로</a>
    </div>
  </div>

  <!-- Scripts -->
  <script src="https://cdn.jsdelivr.net/npm/tom-select@2.3.1/dist/js/tom-select.complete.min.js"></script>
  <script>
    (function(){
      const preview = document.getElementById('previewBox');
      const errorEl = document.getElementById('svcError');
      const btn     = document.getElementById('googleBtn');

      // Tom Select 초기화
      const ts = new TomSelect('#svcSelect', {
        plugins: ['clear_button','dropdown_input'],
        searchField: ['text','name','clbck'],
        maxOptions: 500,
        create: false,
        render: {
          option: (data, escape) => {
            const name = data.name || data.text || '';
            const url  = data.clbck || '';
            return '<div><span class="svc-title">' + escape(name) + '</span>' +
                   (url ? '<span class="svc-sub">' + escape(url) + '</span>' : '') +
                   '</div>';
          },
          item: (data, escape) => {
            const name = data.name || data.text || '';
            const url  = data.clbck || '';
            return '<div><span class="svc-title">' + escape(name) + '</span>' +
                   (url ? '<span class="svc-sub">' + escape(url) + '</span>' : '') +
                   '</div>';
          }
        },
        onChange: updatePreview
      });

      function updatePreview(){
        const value = ts.getValue();
        if(!value){
          preview.textContent = JSON.stringify({ srvcSeq:null, srvcNm:null, clbckUrl:null }, null, 2);
          errorEl.style.display = 'none';
          return;
        }
        const opt = ts.options[value];
        const data = {
          srvcSeq: value || null,
          srvcNm : (opt && (opt.name || opt.text)) || null,
          clbckUrl: (opt && opt.clbck) || null
        };
        preview.textContent = JSON.stringify(data, null, 2);
        errorEl.style.display = 'none';
      }
      updatePreview();

      btn.addEventListener('click', function(){
        const value = ts.getValue();
        if(!value){
          errorEl.style.display = 'block';
          ts.focus();
          return;
        }
        const opt = ts.options[value];
        const params = new URLSearchParams({
          srvcSeq: value,
          clbckUrl: (opt && opt.clbck) || ''
        });
        window.location.href = '/oauth2/authorization/google?' + params.toString();
      });
    })();
  </script>
</body>
</html>
