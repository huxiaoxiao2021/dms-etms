(function() {
    let ClickFrequencyUtil = {};

    function clickRestrict(clickId) {

        let interval = getUccConfigClickInterval();

        if (interval === 0 || interval === '' || interval === -1) {
            return true;
        }

        let time = microTime();
        let repeatTempArr = JSON.parse(sessionStorage.getItem('repeatTemp'));
        let arrLength = 0;
        if (repeatTempArr !== undefined && repeatTempArr !== null) {
            arrLength = repeatTempArr.length;
        }
        if (arrLength === 0) {
            let obj = {
                'clickId': clickId,
                'clickTime': time
            };
            sessionStorage.setItem('repeatTemp', JSON.stringify([obj]));
            // console.info("初始化");
            return false;
        }
        else {
            for (let i = 0; i < arrLength; i++) {
                if (repeatTempArr[i].clickId === clickId) {

                    let existRecord = repeatTempArr[i];

                    let ts = interval - (time - existRecord.clickTime);
                    ts = parseInt(ts / 1000);
                    if (ts > 0) {
                        // console.info("剩余：" + ts + "秒");
                        return true;
                    }
                    else {
                        repeatTempArr.splice(i, 1);
                        existRecord.clickTime = time;
                        repeatTempArr.push(existRecord);
                        sessionStorage.setItem('repeatTemp', JSON.stringify(repeatTempArr));
                        // console.info("更新时间");
                        return false;
                    }
                }
            }

            repeatTempArr.push({
                'clickId': clickId,
                'clickTime': time
            });
            sessionStorage.setItem('repeatTemp', JSON.stringify(repeatTempArr));
            // console.info("初始化");
            return false;
        }
    }

    function microTime() {

        return new Date().getTime();
    }

    function getUccConfigClickInterval () {
        let interval = 0;
        $.ajaxHelper.doPostSync('/common/getClickInterval',  {},function(res){
            if (res && res.succeed) {
                if (res.data) {
                    interval = res.data;
                }
            }
        });

        return interval * 1000;
    }

    ClickFrequencyUtil.controlClick = function (formId, target) {

        let uniqId;
        if (formId) {
            console.info('form:' + formId.serialize());
            uniqId = $.md5(formId.serialize())
        }
        if (uniqId === undefined || uniqId === '') {
            console.info('target:' + target.id);
            if (target) {
                uniqId = target
            }
        }
        if (uniqId) {
            console.info('uniqId:' + uniqId);
            if (clickRestrict(uniqId)) {
                Jd.alert("请勿频繁查询！");
                return true;
            }
            else {
                return false;
            }
        }
    };

    window.ClickFrequencyUtil = ClickFrequencyUtil;
})();
