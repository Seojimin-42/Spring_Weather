// /static/js/script.js

$(() => {
    const $input = $('#region-input');
    const $suggestBox = $('#suggest-box');
    const $suggestList = $('#suggest-list');

    function showSuggest() {
        $suggestBox.removeClass('hidden');
    }

    function hideSuggest() {
        $suggestBox.addClass('hidden');
    }

    // 입력할 때마다 자동완성 요청
    $input.on('input', () => {
        const q = $input.val().trim();
        console.log('[suggest] q =', q);

        if (q.length === 0) {
            hideSuggest();
            return;
        }

        $.ajax({
            url: '/search/suggest',
            method: 'GET',
            data: { q },          // => /search/suggest?q=중구
            dataType: 'json',     // JSON으로 받겠다고 명시
            success: (items) => {
                console.log('[suggest] items =', items);
                renderSuggest(items);
            },
            error: (xhr, status, err) => {
                console.error('[suggest] error:', status, err);
                hideSuggest();
            }
        });
    });

    function renderSuggest(items) {
        $suggestList.empty();

        if (!items || items.length === 0) {
            hideSuggest();
            return;
        }

        items.forEach((r) => {
            const $li = $(`
                <li class="suggest-item">
                    <div class="suggest-region">${r.parentRegion} ${r.childRegion}</div>
                    <div class="suggest-sub">날씨 보기</div>
                </li>
            `);

            // 마우스로 누를 때 바로 이동 (blur보다 먼저 실행되라고 mousedown 사용)
            $li.on('mousedown', () => {
                const city = encodeURIComponent(r.parentRegion);
                const district = encodeURIComponent(r.childRegion);
                window.location.href = `/region/${city}/${district}`;
            });

            $suggestList.append($li);
        });

        showSuggest();
    }

    // 인풋에서 포커스 빠지면 조금 있다가 자동완성 닫기
    $input.on('blur', () => {
        setTimeout(hideSuggest, 150);
    });

    // 다시 포커스 들어오면 값이 있고 리스트가 있으면 다시 보여주기
    $input.on('focus', () => {
        if ($input.val().trim().length > 0 && $suggestList.children().length > 0) {
            showSuggest();
        }
    });
});
