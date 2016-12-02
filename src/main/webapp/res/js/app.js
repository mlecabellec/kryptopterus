/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//Require JST, lodash and JQuery


this["APP"] = this["APP"] || {
    moduleInfo:
            {
                moduleId: "APP",
                moduleVersion: "0.0.1-DEV",
                description: "Root of the APP tree"
            },
    loadedModules: [],
    bootstrap: function bootstrap() {
        APP.loadedModules = APP.loadedModules.concat(APP.moduleInfo);
    }
};
APP.bootstrap();
//------------------------------------------------------------------------------

this.APP["login"] = this.APP["login"] || {
    moduleInfo:
            {
                moduleId: "APP.Login",
                moduleVersion: "0.0.2-DEV",
                description: "Login related module"
            },
    bootstrap: function bootstrap() {
        APP.loadedModules = APP.loadedModules.concat(APP.login.moduleInfo);
    },
    gui: {
        init: function init()
        {

            $("#doLoginButton").unbind("click");
            $("#doLoginButton").on("click", APP.login.gui.displayLoginModal);
            $("#loginButton").unbind("click");
            $("#loginButton").on("click", APP.login.gui.doLoginStage1);
            APP.login.gui.updateMe();
        },
        displayLoginModal: function displayLoginModal()
        {

            $("#loginModal").modal();
        },
        doLoginStage1: function doLoginStage1()
        {

            $("#loginModal").modal('hide');
            var me = {
                username: $("#usernameField").val(),
                password: $("#password1Field").val()
            };
            $("#password1Field").val("");
            var loginJqxhr = $.ajax({
                url: "s/msg/me",
                method: "POST",
                cache: false,
                data: JSON.stringify(me),
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                crossDomain: false,
                success: APP.login.gui.doLoginStage2
            });
        },
        doLoginStage2: function doLoginStage2(me)
        {
            $("#meLabel").text(me.username);
            APP.login.data.me = me;
            //TODO ?

        },
        updateMe: function updateMe()
        {
            var meJqxhr = $.ajax({
                url: "s/msg/me",
                method: "GET",
                cache: false,
                dataType: "json",
                crossDomain: false,
                success: APP.login.gui.updateMeLabel
            });
            //TODO ?

        },
        updateMeLabel: function updateMeLabel(me)
        {
            $("#meLabel").text(me.username);
            APP.login.data.me = me;
            //TODO ?

        }
    },
    data: {
        me: {}
    }
};
APP.login.bootstrap();
//------------------------------------------------------------------------------

this.APP["debug"] = this.APP["debug"] || {
    moduleInfo:
            {
                moduleId: "APP.debug",
                moduleVersion: "0.0.1-DEV",
                description: "Debug related functions"
            },
    bootstrap: function bootstrap() {
        APP.loadedModules = APP.loadedModules.concat(APP.debug.moduleInfo);
        if (APP.debug.enabled)
        {
            APP.debug.debugKit.init();
        }
    },
    enabled: true,
    debugKit: {
        init: function initDebugKit()
        {

        }
    }
};
APP.debug.bootstrap();
//------------------------------------------------------------------------------

this.APP["logs"] = this.APP["logs"] || {
    moduleInfo:
            {
                moduleId: "APP.logs",
                moduleVersion: "0.0.1-DEV",
                description: "Log related functions"
            },
    bootstrap: function bootstrap() {
        APP.loadedModules = APP.loadedModules.concat(APP.logs.moduleInfo);
    }
};
APP.logs.bootstrap();
//------------------------------------------------------------------------------

this.APP["home"] = this.APP["home"] || {
    moduleInfo:
            {
                moduleId: "APP.home",
                moduleVersion: "0.0.2-DEV",
                description: "Home functions including init routines"
            },
    bootstrap: function bootstrap() {
        APP.loadedModules = APP.loadedModules.concat(APP.home.moduleInfo);
    },
    gui: {
        init: function init() {

            //$("#changeAndImpactsLink").unbind("click");
            //$("#changeAndImpactsLink").on("click", APP.home.gui.setupChangeAndImpactsWorkshop);

            APP.home.gui.initQuillEditor_v1();
            APP.home.gui.getMessages(0, 50);
        },
        initQuillEditor_v1: function initQuillEditor_v1() {

            $("#publishButton").unbind("click");
            $("#publishButton").on("click", APP.home.gui.doPostMessage);

            // Initialize editor with custom theme and modules
            APP.home.data.mainQuillEditor = new Quill("#quillEditor", {
                modules: {
                    "toolbar": {container: "#quillEditorToolbar"}
                },
                theme: "snow"
            });
        },
        getMessages: function getMessages(offset, count) {

            var loginJqxhr = $.ajax({
                url: "s/msg/get",
                method: "POST",
                cache: false,
                data: JSON.stringify({offset: offset, count: count}),
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                crossDomain: false,
                success: APP.home.gui.proceedWithMessages
            });
        },
        proceedWithMessages: function proceedWithMessages(messageList)
        {
            var messageStackContainer = $("#messageStackContainer");
            APP.home.data.messages = APP.home.data.messages.concat(messageList);

            APP.home.data.messages.sort(function sortMessages(a, b) {
                return b.creationDate - a.creationDate;
            });
            for (var i = 0; i < (APP.home.data.messages.length - 1); i++)
            {
                var cMsg = APP.home.data.messages[i];
                var nMsg = APP.home.data.messages[i + 1];
                if (cMsg.id == nMsg.id)
                {
                    APP.home.data.messages.splice(i + 1, 1);
                }

            }

            APP.home.data.messages.forEach(function handleMessageDsplay(cMsg, cIndex, cArray)
            {

                if ($("#msg" + cMsg.id).size() == 0)
                {

                    var newMessageBloc = $("<div/>");
                    newMessageBloc.addClass("message");
                    newMessageBloc.addClass("panel");
                    newMessageBloc.addClass("panel-default");
                    newMessageBloc.attr("id", "msg" + cMsg);
                    newMessageBloc.attr("data-id", "" + cMsg);
                    newMessageBloc.attr("data-timestamp", cMsg.creationDate);
                    var newMessageHeading = $("<div/>");
                    newMessageHeading.addClass("panel-heading");
                    newMessageBloc.append(newMessageHeading);
                    var newMessageTitle = $("<h3/>");
                    newMessageTitle.addClass("panel-title");
                    newMessageTitle.text("From " + cMsg.username + ", at " + new Date(cMsg.creationDate).toLocaleString());
                    newMessageHeading.append(newMessageTitle);
                    var newMessageBody = $("<div/>");
                    newMessageBody.addClass("panel-body");
                    newMessageBody.html(cMsg.content);
                    newMessageBloc.append(newMessageBody);
                    messageStackContainer.append(newMessageBloc);
                    if ($(".message").size() > 0)
                    {

                        var messageBlocs = $(".message");

                        messageBlocs.each(function findRightInsertionPoint(cBlocIdx, cBlocElem)
                        {

                            var jCurrentBlocElem = $(cBlocElem)
                            var jPreviousBlocElem = $(cBlocElem).prev();
                            var jNextBlocElem = $(cBlocElem).next();
                            var currentTimestamp = jCurrentBlocElem.attr("data-timestamp");
                            var previousTimestamp = 9999999999999999;
                            var nextTimestamp = -1;

                            if (jPreviousBlocElem.size() > 0)
                            {
                                previousTimestamp = jPreviousBlocElem.attr("data-timestamp");
                            }

                            if (jNextBlocElem.size() > 0)
                            {
                                nextTimestamp = jNextBlocElem.attr("data-timestamp");
                            }


                            if ((jNextBlocElem.size() == 0) && (jPreviousBlocElem.size() == 0))
                            {
                                if (cMsg.creationDate > currentTimestamp)
                                {
                                    jCurrentBlocElem.insertBefore(newMessageBloc);
                                }

                                if (cMsg.creationDate < currentTimestamp)
                                {
                                    jCurrentBlocElem.insertAfter(newMessageBloc);
                                }
                            }

                            if ((previousTimestamp > cMsg.creationDate) && (cMsg.creationDate > currentTimestamp))
                            {

                                jCurrentBlocElem.insertBefore(newMessageBloc);
                            }

                            if ((currentTimestamp > cMsg.creationDate) && (cMsg.creationDate > nextTimestamp))
                            {

                                jCurrentBlocElem.insertAfter(newMessageBloc);
                            }

                            if (currentTimestamp == cMsg.creationDate)
                            {
                                jCurrentBlocElem.insertBefore(newMessageBloc);
                            }

                        });
                    } else
                    {
                        messageStackContainer.append(newMessageBloc);
                    }

                }
            });

        },
        doPostMessage: function doPostMessage()
        {
            APP.login.gui.updateMe();

            var newMsg = {id: 0, content: "", creationDate: new Date(), modificationDate: new Date()};

            newMsg.content = APP.home.data.mainQuillEditor.getHTML();

            if (APP.login.data.me.isAnonymous)
            {
                $("#publishButton").attr("data-content", "You must be logged in for publication !");
                $("#publishButton").attr("data-placement", "left");
                $("#publishButton").popover("show");

                $("#doLoginButton").attr("data-content", "You must be logged in for publication !");
                $("#doLoginButton").attr("data-placement", "bottom");
                $("#doLoginButton").popover("show");
            } else
            {
                $("#publishButton").popover("hide");
                $("#doLoginButton").popover("hide");
                APP.home.gui.postMessage(newMsg);

            }
        },
        postMessage: function postMessage(newMsg)
        {
            var postMsgJqxhr = $.ajax({
                url: "s/msg/post",
                method: "POST",
                cache: false,
                data: JSON.stringify(newMsg),
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                crossDomain: false,
                success: APP.home.gui.afterPostMessage
            });

        },
        afterPostMessage: function afterPostMessage(msg)
        {

            APP.home.data.mainQuillEditor.setHTML("");
            APP.home.gui.getMessages(0, 50);
            $("#publishButton").popover("hide");
            $("#doLoginButton").popover("hide");
        }
    },
    toolKit: {},
    data: {
        mainQuillEditor: {},
        messages: new Array(0)}
};
APP.home.bootstrap();


$(function onReadyHandler()
{
    APP.login.gui.init();
    APP.home.gui.init();
});
