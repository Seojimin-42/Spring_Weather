// /static/js/script.js

$(function () {
    const $input = $('#search-input');   // 현재 HTML에 있는 id
    const $list  = $('#suggest-list');   // 이게 곧 표시/숨길 박스 역할

    function showSuggest() {
        $list.removeClass('hidden');
    }

    function hideSuggest() {
        $list.addClass('hidden').empty();   // 숨기면서 내용도 비우기
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
            dataType: 'json',
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
        $list.empty();

        if (!items || items.length === 0) {
            hideSuggest();
            return;
        }

        items.forEach((r) => {
            const $li = $(`
                <li class="px-3 py-1 cursor-pointer bg-white hover:bg-slate-100">
                    ${r.parentRegion} ${r.childRegion}
                </li>
            `);

            // 클릭하면 검색창에 넣고 폼 제출
            $li.on('mousedown', () => {
                $('#search-input').val(`${r.parentRegion} ${r.childRegion}`);

                const form = document.getElementById('search-form');
                if (form) form.submit();

                hideSuggest();
            });

            $list.append($li);
        });

        showSuggest();
    }

    // 인풋 밖을 클릭하면 닫기
    $(document).on('click', (e) => {
        if (!$(e.target).closest('#search-input, #suggest-list').length) {
            hideSuggest();
        }
    });
});
