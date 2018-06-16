//存放主要交互逻辑的js代码

var seckill = {

    //封装秒杀相关ajax的url
    URL: {
        now: function () {
            return '/seckill/secondKill/time/now';
        },
        
        exposer: function (seckillId) {
            return '/seckill/secondKill/' + seckillId + '/exposer';
        },
        
        execution: function (seckillId, md5) {
            return '/seckill/secondKill/' + seckillId + '/' + md5 + '/execution';
        }
    },

    //验证手机号
    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        } else {
            return false;
        }
    },

    //详情页秒杀逻辑
    detail: {
        //详情页初始化
        init: function (params) {
            //在cookie中查找手机号
            var userPhone = $.cookie('userPhone');
            
            //验证cookie中的userPhone是否存在
            if (!seckill.validatePhone(userPhone)) {
                //如果不存在则绑定手机 控制输出
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true,//显示弹出层
                    backdrop: 'static',//禁止位置关闭
                    keyboard: false//关闭键盘事件
                });

                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    //验证手机号格式是否输入正确
                    if (seckill.validatePhone(inputPhone)) {
                        //手机号写入cookie(7天过期)
                        $.cookie('userPhone', inputPhone, {expires: 7, path: '/seckill'});
                        //验证通过　刷新页面
                        window.location.reload();
                    } else {
                        //验证不通过则给出错误提示
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误!</label>').show(300);
                    }
                });
            }
            
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            //已经登录,计时交互
            $.get(seckill.URL.now(), {}, function (result) {
                if (result && result['success']) {
                    var nowTime = result['data'];
                    //时间判断 计时交互
                    seckill.countDown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log('获取系统时间失败！');
                }
            });
        }
    },
        
    handlerSeckill: function (seckillId, node) {
        //获取秒杀地址,控制显示器,执行秒杀
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');

        $.get(seckill.URL.exposer(seckillId), {}, function (result) {
            //在回调函数中执行交互流程
            if (result && result['success']) {
                var exposer = result['data'];
             
                if (exposer['exposer']) {
                    //开启秒杀,获取秒杀地址
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.execution(seckillId, md5);
                    console.log("killUrl: " + killUrl);
                    //绑定一次点击事件
                    $('#killBtn').one('click', function () {
                        //执行秒杀请求
                        //1.先禁用按钮
                        $(this).addClass('disabled');//,<-$(this)===('#killBtn')->
                        //2.发送秒杀请求执行秒杀
                        $.post(killUrl, {}, function (result) {
                            if (result && result['success']) {
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                console.log("state =" + state);
                                console.log("stateInfo =" + stateInfo);
                                //显示秒杀结果
                                node.html('<span class="label label-success">' + stateInfo + '</span>');
                            }
                        });
                    });
                    node.show();
                } else {
                    //未开启秒杀(浏览器计时偏差)
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    seckill.countDown(seckillId, now, start, end);
                }
            } else {
                console.log('result: ' + result);
            }
        });

    },

    countDown: function (seckillId, nowTime, startTime, endTime) {
        console.log("seckillId: " + seckillId + ' nowTime: ' + nowTime + ' startTime: ' + startTime + ' endTime: ' + endTime);
        var seckillBox = $('#seckill-box');
        
        if (nowTime > endTime) { //如果系统时间大于秒杀结束时间，秒杀结束
            seckillBox.html('秒杀结束!');
        } else if (nowTime < startTime) { //如果系统时间小于秒杀开始时间，秒杀未开始
            //倒计时事件绑定
            var killTime = new Date(startTime + 1000);//todo 防止时间偏移
            seckillBox.countdown(killTime, function (event) {
                //时间格式
                var format = event.strftime('秒杀倒计时: %D天 %H时 %M分 %S秒 ');
                seckillBox.html(format);
            }).on('finish.countdown', function () {
                //时间完成后回调事件
                //获取秒杀地址,控制现实逻辑,执行秒杀
                console.log('______fininsh.countdown');
                seckill.handlerSeckill(seckillId, seckillBox);
            });
        } else {
            //秒杀已开始
            seckill.handlerSeckill(seckillId, seckillBox);
        }
    }

}