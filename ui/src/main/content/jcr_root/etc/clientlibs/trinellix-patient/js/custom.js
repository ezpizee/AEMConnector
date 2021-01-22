"use strict";

let Mindful = (function ($) {
  let _ = {},
      _private = {},
      allCardTips = [],
      containerIds = {
        cardTips: "cardTips",
        userCardsList: "userCardsList",
        moreTipsList: "moreTipsList",
      },
      attrs = {
        dataHbs: "data-hbs",
        dataCard: "data-card",
        dataCardUUID: "data-card-uuid",
        dataTrigger: "data-trigger",
        dataBtnCard: "data-btn-card",
        dataBtnCardUUID: "data-btn-card-uuid",
      },
      hbsTemplates = { cardTips: null, userCardsList: null },
      elements = {
        cardTips: [],
        userCardsList: [],
        previousBtn: [],
        previousBtnContainer: [],
      },
      fragmentRootPath = "",
      disclaimerPath = "",
      csrfToken = "",
      //csrfTokenPath = "",
      csrfTokenPath = "/libs/granite/csrf/token.json",
      servicePath = "/bin/takeda/mindful",
      //servicePath = "/takeda-trinellix-patient/structures?layout=render&servlet=com.takeda.trintellix.patient.core.servlets.MindfulServlet",
      cookieMaxLife = 365,
      mindfulTipCookieName = "mindful_tip_",
      mindfulCDMIDCookieName = "mindful_cdmid",
      mindfulMCIDCookieName = "mindful_mcid",
      mindfulUserCookieName = "mindful_user_engagement",
      mindfulVisitedUserCookieName = "mindful_user_visited",
      userData = {mcid: "", cdmid: "", userId: "", channel: "web", engagement: {}},
      logic = {isRegistered: false, hasCDMID: false, hasMCID: false, state: "", isReturnedUser: false},
      states = {firstTimePageVisit: "first_time_page_visit", visitMM: "visit_mm", personalizedMMPage: "personalized_mm_page"};

  _.init = function () {
    $(document).ready(function () {
      _private.collapsible();
      fragmentRootPath = $("[data-tips-root]").attr("data-tips-root");
      disclaimerPath = $("[data-disclaimer-path]").attr("data-disclaimer-path");
      $.ajax({
        type: "GET",
        url: servicePath,
        data: {
          scope: containerIds.cardTips,
          fragmentRootPath: fragmentRootPath,
          disclaimerPath: disclaimerPath,
        },
        success: function (resp) {
          if (resp && resp.hasOwnProperty("cards") && resp.hasOwnProperty("isRegistered")) {
            userData.userId = resp.hasOwnProperty("userId") ? resp.userId : userData.userId;
            allCardTips = resp.cards && resp.cards.length ? resp.cards : [];
            logic.isRegistered = resp.isRegistered;
            logic.isReturnedUser = _private.cookie.exists(mindfulVisitedUserCookieName);
            if (!logic.isReturnedUser) {
              _private.cookie.set({name: mindfulVisitedUserCookieName, value: "1", days: cookieMaxLife});
            }
            else {
              logic.state = states.visitMM;
            }
            // get data if comes from lead gen or email channel
            if (_private.uri.queryStringExists("mcid")) {
              let mcid = _private.uri.getQueryString('mcid');
              if (mcid) {
                if (_private.uri.getQueryString('channel')) {
                  userData.channel = _private.uri.getQueryString('channel');
                }
                userData.mcid = mcid;
                logic.hasCDMID = true;
                logic.state = states.visitMM;
                logic.isReturnedUser = true;
                _private.cookie.set({name: mindfulMCIDCookieName, value: mcid, days: cookieMaxLife});
              }
            }
            if (_private.cookie.exists(mindfulUserCookieName)) {
              let userDataInCookie = _private.toJSONObject(_private.cookie.get(mindfulUserCookieName));
              if (!userData.mcid) {
                userData.mcid = userDataInCookie.mcid||userData.mcid;
              }
              userData.cdmid = userDataInCookie.cdmid||userData.cdmid;
              userData.userId = userDataInCookie.userId||userData.userId;
              userData.channel = userDataInCookie.channel||userData.channel;
            }
            if (userData.mcid) {
              logic.hasMCID = true;
            }
            else {
              userData.mcid = _private.cookie.get(mindfulMCIDCookieName)||"";
              if (userData.mcid) {
                logic.hasMCID = true;
              }
            }
            if (userData.cdmid) {
              logic.hasCDMID = true;
            }
            else {
              userData.cdmid = _private.cookie.get(mindfulCDMIDCookieName)||"";
              if (userData.cdmid) {
                logic.hasCDMID = true;
              }
            }
            _private.checkUser();
          }
        },
      });
      if (csrfTokenPath) {
        _private.getCSRFToken();
        setInterval(_private.getCSRFToken, 10 * 60 * 1000);
      }
    });
  };

  _private.checkUser = function () {
    // 1) if return user
    if (logic.isReturnedUser) {
      // 1.1) if both cdmid and cdid exist
      if (logic.hasMCID && logic.hasCDMID) {
        _private.actions.associateMCIDAndCDMID(userData.cdmid, userData.mcid, function(resp){
          _private.actions.retrieveEngagementByCDMID(userData.cdmid, function(resp){
            logic.state = states.personalizedMMPage;
            _private.views.cards();
            _private.cookie.set({name: mindfulCDMIDCookieName, value: userData.cdmid, days: cookieMaxLife});
            _private.cookie.set({name: mindfulMCIDCookieName, value: userData.mcid, days: cookieMaxLife});
            _private.setUserDataCookie();
            // API Call based on MCID / CDM ID combination store engagement
            // todo: check with Rohit / Klick
          });
        });
      }
      // 1.2) if exists on mcid
      else if (logic.hasMCID) {
        _private.actions.getCDMID(function({cdmid}) {
          if (cdmid) {
            userData.cdmid = cdmid;
            logic.hasCDMID = true;
            _private.cookie.set({name: mindfulCDMIDCookieName, value: userData.cdmid, days: cookieMaxLife});
            _private.cookie.set({name: mindfulMCIDCookieName, value: userData.mcid, days: cookieMaxLife});
            _private.setUserDataCookie();
            _private.actions.associateMCIDAndCDMID(userData.cdmid, userData.mcid, function(resp) {
              _private.actions.retrieveEngagementByCDMID(userData.cdmid, function (resp) {
                logic.state = states.personalizedMMPage;
                _private.views.cards();
                // API Call based on MCID / CDM ID combination store engagement
                // todo: check with Rohit / Klick
              });
            });
          }
          else {
            _private.cookie.set({name: mindfulMCIDCookieName, value: userData.mcid, days: cookieMaxLife});
            _private.setUserDataCookie();
            _private.actions.retrieveEngagementByMCID(userData.mcid,function(resp){
              logic.state = states.personalizedMMPage;
              _private.views.cards();
              // API Call based on MCID / CDM ID combination store engagement
              // todo: check with Rohit / Klick
            });
          }
        });
      }
      // 1.3) if exists only cdmid
      else if (logic.hasCDMID) {
        _private.actions.retrieveEngagementByCDMID(userData.cdmid,function(resp){
          logic.state = states.personalizedMMPage;
          _private.views.cards();
          _private.cookie.set({name: mindfulCDMIDCookieName, value: userData.cdmid, days: cookieMaxLife});
          _private.setUserDataCookie();
          // API Call based on MCID / CDM ID combination store engagement
          // todo: check with Rohit / Klick
        });
      }
      // 1.4) if exist none of them
      else {
        // 1.4.1) first page visit
        logic.state = states.firstTimePageVisit;
        // 1.4.2) API Call - MC ID Generated
        _private.actions.getMCID(function({mcid}){
          if (mcid) {
            userData.mcid = mcid;
            logic.hasMCID = true;
            _private.cookie.set({name: mindfulMCIDCookieName, value: userData.mcid, days: cookieMaxLife});
            _private.setUserDataCookie();
          }
          _private.views.cards();
        });
      }
    }
    // 2) if a registered user
    else if (logic.isRegistered) {
      // 2.1) API Call - CDM ID Generated
      _private.actions.getCDMID(function({cdmid}){
        if (cdmid) {
          _private.cookie.set({name: mindfulCDMIDCookieName, value: userData.cdmid, days: cookieMaxLife});
          _private.setUserDataCookie();
          userData.cdmid = cdmid;
          logic.hasCDMID = true;
          // 2.2) MM first time Page Visit
          logic.state = states.firstTimePageVisit;
          // 2.3) API Call - MC ID Generated
          _private.actions.getMCID(function({mcid}){
            if (mcid) {
              userData.mcid = mcid;
              logic.hasMCID = true;
              _private.cookie.set({name: mindfulMCIDCookieName, value: userData.mcid, days: cookieMaxLife});
              _private.setUserDataCookie();
            }
            _private.views.cards();
          });
        }
        else {
          _private.cookie.set({name: mindfulCDMIDCookieName, value: userData.cdmid, days: cookieMaxLife});
          _private.setUserDataCookie();
          _private.views.cards();
        }
      });
    }
    // 3) if not a registered user
    else {
      // 3.1) MM first time Page Visit
      logic.state = states.firstTimePageVisit;
      // 3.2) API Call - MC ID Generated
      _private.actions.getMCID(function({mcid}){
        if (mcid) {
          userData.mcid = mcid;
          logic.hasMCID = true;
          _private.cookie.set({name: mindfulMCIDCookieName, value: userData.mcid, days: cookieMaxLife});
          _private.setUserDataCookie();
        }
        _private.views.cards();
      });
    }
  };

  _private.actions = (function () {
    let action = {};

    action.associateMCIDAndCDMID = function(cdmid, mcid, callback) {
      cdmid = cdmid||userData.cdmid;
      mcid = mcid||userData.mcid;
      if (cdmid && mcid) {
        if (csrfTokenPath && !csrfToken) {
          _private.getCSRFToken(function (resp) {
            if (resp && resp.token) {
              csrfToken = resp.token;
              post();
            }
          });
        } else {
          post();
        }
      }
      function post() {
        $.ajax({
          type: "POST",
          url: servicePath,
          data: {
            scope: "associateMCIDAndCDMID",
            cdmid: cdmid,
            mcid: mcid,
            fragmentRootPath: fragmentRootPath,
            disclaimerPath: disclaimerPath,
          },
          beforeSend: _private.beforeSend,
          success: function (resp) {
            if (resp) {
              if (typeof callback === "function") {
                callback(resp);
              }
            }
          },
        });
      }
    };

    action.retrieveEngagementByCDMID = function(cdmid, callback) {
      cdmid = cdmid||userData.cdmid;
      if (cdmid) {
        if (csrfTokenPath && !csrfToken) {
          _private.getCSRFToken(function (resp) {
            if (resp && resp.token) {
              csrfToken = resp.token;
              post();
            }
          });
        } else {
          post();
        }
      }
      function post() {
        $.ajax({
          type: "POST",
          url: servicePath,
          data: {
            scope: "retrieveEngagementByCDMID",
            cdmid: cdmid,
            fragmentRootPath: fragmentRootPath,
            disclaimerPath: disclaimerPath,
          },
          beforeSend: _private.beforeSend,
          success: function (resp) {
            if (resp) {
              if (typeof callback === "function") {
                callback(resp);
              }
              if (resp.engagement) {
                userData.engagement = resp.engagement;
                _private.setUserDataCookie();
              }
            }
          },
        });
      }
    };

    action.retrieveEngagementByMCID = function(mcid, callback) {
      mcid = mcid||userData.mcid;
      if (mcid) {
        if (csrfTokenPath && !csrfToken) {
          _private.getCSRFToken(function (resp) {
            if (resp && resp.token) {
              csrfToken = resp.token;
              post();
            }
          });
        } else {
          post();
        }
      }
      function post() {
        $.ajax({
          type: "POST",
          url: servicePath,
          data: {
            scope: "retrieveEngagementByMCID",
            mcid: mcid,
            fragmentRootPath: fragmentRootPath,
            disclaimerPath: disclaimerPath,
          },
          beforeSend: _private.beforeSend,
          success: function (resp) {
            if (resp) {
              if (typeof callback === "function") {
                callback(resp);
              }
              if (resp.engagement) {
                userData.engagement = resp.engagement;
                _private.setUserDataCookie();
              }
            }
          },
        });
      }
    };

    action.storeEngagementWithMCIDAndCDMID = function ({ ctaid, mcid, cdmid, userId }) {
      if (csrfTokenPath && !csrfToken) {
        _private.getCSRFToken(function (resp) {
          if (resp && resp.token) {
            csrfToken = resp.token;
            post();
          }
        });
      } else {
        post();
      }
      function post() {
        $.ajax({
          type: "POST",
          url: servicePath,
          data: {
            scope: "storeEngagementWithMCIDAndCDMID",
            engagement: { ctaid, mcid, cdmid, userId },
            fragmentRootPath: fragmentRootPath,
            disclaimerPath: disclaimerPath,
          },
          beforeSend: _private.beforeSend,
          success: function (resp) {
            if (resp.engagement) {
              console.log(resp.engagement);
              userData.engagement = resp.engagement;
              _private.setUserDataCookie();
            }
          },
        });
      }
    };

    action.storeEngagementWithMCID = function ({ ctaid, mcid, userId }) {
      if (csrfTokenPath && !csrfToken) {
        _private.getCSRFToken(function (resp) {
          if (resp && resp.token) {
            csrfToken = resp.token;
            post();
          }
        });
      } else {
        post();
      }
      function post() {
        $.ajax({
          type: "POST",
          url: servicePath,
          data: {
            scope: "storeEngagementWithMCID",
            engagement: { ctaid, mcid, userId },
            fragmentRootPath: fragmentRootPath,
            disclaimerPath: disclaimerPath,
          },
          beforeSend: _private.beforeSend,
          success: function (resp) {
            if (resp.engagement) {
              console.log(resp.engagement);
              userData.engagement = resp.engagement;
              _private.setUserDataCookie();
            }
          },
        });
      }
    };

    action.getMCID = function (callback) {
      if (csrfTokenPath && !csrfToken) {
        _private.getCSRFToken(function (resp) {
          if (resp && resp.token) {
            csrfToken = resp.token;
            post();
          }
        });
      } else {
        post();
      }
      function post() {
        let mcid = _private.cookie.get(mindfulMCIDCookieName);
        if (!mcid) {
          action.genMCID(callback);
        }
        else if (typeof callback === "function") {
          callback({mcid: mcid});
        }
      }
    };

    action.genMCID = function (callback) {
      if (csrfTokenPath && !csrfToken) {
        _private.getCSRFToken(function (resp) {
          if (resp && resp.token) {
            csrfToken = resp.token;
            post();
          }
        });
      } else {
        post();
      }
      function post() {
        $.ajax({
          type: "POST",
          url: servicePath,
          data: {
            scope: "genMCID",
            fragmentRootPath: fragmentRootPath,
            disclaimerPath: disclaimerPath,
          },
          beforeSend: _private.beforeSend,
          success: function (resp) {
            if (resp.mcid) {
              if (resp.hasOwnProperty("isRegistered")) {
                logic.isRegistered = resp.isRegistered;
              }
              if (resp.hasOwnProperty("userId")) {
                userData.userId = resp.userId;
              }
              _private.cookie.set({name: mindfulMCIDCookieName, value: resp.mcid, days: cookieMaxLife});
              _private.setUserDataCookie();
              if (typeof callback === "function") {
                callback(resp);
              }
            }
          },
        });
      }
    };

    action.getCDMID = function (callback) {
      if (csrfTokenPath && !csrfToken) {
        _private.getCSRFToken(function (resp) {
          if (resp && resp.token) {
            csrfToken = resp.token;
            post();
          }
        });
      } else {
        post();
      }
      function post() {
        let cdmid = _private.cookie.get(mindfulCDMIDCookieName);
        if (!cdmid) {
          action.genCDMID(callback);
        }
        else if (typeof callback === "function") {
          callback({cdmid: cdmid});
        }
      }
    };

    action.genCDMID = function (callback) {
      if (csrfTokenPath && !csrfToken) {
        _private.getCSRFToken(function (resp) {
          if (resp && resp.token) {
            csrfToken = resp.token;
            post();
          }
        });
      } else {
        post();
      }
      function post() {
        $.ajax({
          type: "POST",
          url: servicePath,
          data: {
            scope: "genCDMID",
            fragmentRootPath: fragmentRootPath,
            disclaimerPath: disclaimerPath,
          },
          beforeSend: _private.beforeSend,
          success: function (resp) {
            if (resp.cdmid) {
              if (resp.hasOwnProperty("isRegistered")) {
                logic.isRegistered = resp.isRegistered;
              }
              if (resp.hasOwnProperty("userId")) {
                userData.userId = resp.userId;
              }
              _private.cookie.set({name: mindfulCDMIDCookieName, value: resp.cdmid, days: cookieMaxLife});
              _private.setUserDataCookie();
              if (typeof callback === "function") {
                callback(resp);
              }
            }
          },
        });
      }
    };

    action.registerUser = function (callback) {
      if (csrfTokenPath && !csrfToken) {
        _private.getCSRFToken(function (resp) {
          if (resp && resp.token) {
            csrfToken = resp.token;
            post();
          }
        });
      } else {
        post();
      }
      function post() {
        $.ajax({
          type: "POST",
          url: servicePath,
          data: {
            scope: "registerUser",
            formData: {'todo':'todo'},
            fragmentRootPath: fragmentRootPath,
            disclaimerPath: disclaimerPath,
          },
          beforeSend: _private.beforeSend,
          success: function (resp) {
            if (resp.hasOwnProperty("isRegistered")) {
              logic.isRegistered = resp.isRegistered;
            }
            if (resp.hasOwnProperty("userId")) {
              userData.userId = resp.userId;
            }
            callback(resp);
          },
        });
      }
    };

    action.like = function (uuid, nextCardEle, toggleCard, cardPosition) {
      // look for next element and the uuid of the current element
      if (toggleCard !== false) {
        if (nextCardEle.length) {
          _private.views.toggleCard(elements.cardTips, nextCardEle);
        }
      }
      if (uuid) {
        if (csrfTokenPath && !csrfToken) {
          _private.getCSRFToken(function (resp) {
            if (resp && resp.token) {
              csrfToken = resp.token;
              post();
            }
          });
        } else {
          post();
        }
      }
      function post() {
        $.ajax({
          type: "POST",
          url: servicePath,
          data: {
            scope: "like",
            uuid: uuid,
            fragmentRootPath: fragmentRootPath,
            disclaimerPath: disclaimerPath,
          },
          beforeSend: _private.beforeSend,
          success: function (resp) {
            if (resp.uuid) {
              let cName = _private.cookie.exists(mindfulTipCookieName, resp.uuid);
              if (cName === false) {
                _private.cookie.set({
                  name: _private.randomCookieNameFromPfx(mindfulTipCookieName),
                  value: resp.uuid,
                  days: cookieMaxLife,
                });
              }
              // store engagement
              if (userData.cdmid && userData.mcid) {
                _private.actions.storeEngagementWithMCIDAndCDMID({
                  ...userData,
                  ctaid: resp.uuid,
                });
              }
              else if (userData.mcid) {
                _private.actions.storeEngagementWithMCID({
                  ...userData,
                  ctaid: resp.uuid,
                });
              }
              _private.views.userCardsList();
              // check card at least 3 to display button
              _private.views.togglePrevTipBtn(cardPosition);
            }
          },
        });
      }
    };

    action.dismiss = function (uuid, thisCardEle) {
      let cName = _private.cookie.exists(mindfulTipCookieName, uuid);
      if (uuid && thisCardEle.length && cName && _private.cookie.exists(cName)) {
        if (csrfTokenPath && !csrfToken) {
          _private.getCSRFToken(function (resp) {
            if (resp && resp.token) {
              csrfToken = resp.token;
              post();
            }
          });
        } else {
          post();
        }
      }
      function post() {
        $.ajax({
          type: "POST",
          url: servicePath,
          data: {
            scope: "dismiss",
            uuid: uuid,
            fragmentRootPath: fragmentRootPath,
            disclaimerPath: disclaimerPath,
          },
          beforeSend: _private.beforeSend,
          success: function (resp) {
            if (resp.uuid) {
              _private.views.moreTipsList();
              _private.cookie.delete(cName);
              thisCardEle.remove();
              _private.views.togglePrevTipBtn();
              _private.views.userCardsList();
            }
          }
        });
      }
    };

    action.addReminder = function (uuid) {
      if (csrfTokenPath && !csrfToken) {
        _private.getCSRFToken(function (resp) {
          if (resp && resp.token) {
            csrfToken = resp.token;
            post();
          }
        });
      } else {
        post();
      }
      function post() {
        if (uuid) {
          $.ajax({
            type: "POST",
            url: servicePath,
            data: {
              scope: "addReminder",
              uuid: uuid,
              fragmentRootPath: fragmentRootPath,
              disclaimerPath: disclaimerPath,
            },
            beforeSend: _private.beforeSend,
            success: function (resp) {
              if (resp.uuid) {
                alert("reminder sucess with uuid " + uuid);
              }
              if (resp.hasOwnProperty("isRegistered")) {
                logic.isRegistered = resp.isRegistered;
              }
            },
          });
        }
      }
    };

    action.previousTip = function () {
      let e = _private.getCurrentCardEle();
      if (e && e.length && e.prev().length) {
        _private.views.toggleCard(elements.cardTips, e.prev());
        _private.views.togglePrevTipBtn(parseInt(e.attr(attrs.dataCard)));
      }
    };

    action.skip = function (uuid, nextCardEle, toggleCard, cardPosition) {
      _private.views.togglePrevTipBtn(cardPosition);
      if (nextCardEle.length) {
        _private.views.toggleCard(elements.cardTips, nextCardEle);
      }
    };

    action.bindClickOnDataTriggerBtn = function (e) {
      if (e && e.length) {
        e.find("[" + attrs.dataTrigger + "]").click(function (e) {
          e.preventDefault();
          let t = $(this),
              uuid = t.attr(attrs.dataBtnCardUUID),
              action = t.attr(attrs.dataTrigger),
              thisCard = $("[" + attrs.dataCardUUID + '="' + uuid + '"]'),
              ele = action === "dismiss" ? thisCard : thisCard.next();
          if (typeof _private.actions[action] !== "undefined") {
            _private.actions[action](
                uuid,
                ele,
                true,
                parseInt(t.attr(attrs.dataBtnCard))
            );
          }
        });
      }
    };

    action.bindClickOnMoreTipsLikeBtns = function () {
      let container = $("#" + containerIds.moreTipsList);
      if (container.length) {
        container.find("[" + attrs.dataTrigger + '="like"]').click(function () {
          let t = $(this),
              uuid = t.attr(attrs.dataBtnCardUUID),
              nextCard = $("[" + attrs.dataCardUUID + '="' + uuid + '"]').next();
          action.like(uuid, nextCard, false);
        });
      }
    };

    return action;
  })();

  _private.views = (function () {
    let view = {};
    view.cards = function () {
      // bind click to previous Btn if exists
      elements.previousBtn = $(
          ".cards [" + attrs.dataTrigger + '="previousTip"]'
      );
      if (elements.previousBtn.length) {
        elements.previousBtnContainer = elements.previousBtn.parent().parent();
        if (elements.previousBtnContainer.length) {
          elements.previousBtn.click(function (e) {
            e.preventDefault();
            _private.actions.previousTip();
          });
          _private.views.togglePrevTipBtn();
        }
      }
      _private.views.userCardsList();
      _private.views.cardTips();
    };
    view.moreTipsList = function () {
      let container = $("#" + containerIds.moreTipsList);
      if (container.length) {
        container.html(
            hbsTemplates["userCardsList"]({
              moreTips: true,
              cards: _private.getMoreTipCards(),
              isRegistered: logic.isRegistered
            })
        );
        if (container.find("[" + attrs.dataTrigger + '="dismiss"]').length) {
          container
              .find("[" + attrs.dataTrigger + '="dismiss"]')
              .each(function () {
                let t = $(this);
                t.attr(attrs.dataTrigger, "like");
                t.attr("class", "btn btn-outline-info");
                t.attr("href", "javascript:void(0)");
                t.html('<i class="far fa-heart"></i> Like');
              });
          container.find("[" + attrs.dataTrigger + '="addReminder"]').remove();
          _private.actions.bindClickOnMoreTipsLikeBtns();
        }
      }
    };
    view.userCardsList = function (key) {
      if (allCardTips.length) {
        key = key || containerIds.userCardsList;
        if (!hbsTemplates[key]) {
          hbsTemplates[key] = Handlebars.compile(
              $("script[" + attrs.dataHbs + '="' + key + '"]').html()
          );
        }
        if (hbsTemplates[key]) {
          elements[key] = $("#" + key);
          elements[key].html(
              hbsTemplates[key]({
                cards: _private.getAllUserCurrentCards(),
                isRegistered: logic.isRegistered
              })
          );
          _private.views.moreTipsList();
          _private.actions.bindClickOnDataTriggerBtn(elements[key]);
        }
      }
    };
    view.cardTips = function (key) {
      if (allCardTips.length) {
        key = key || containerIds.cardTips;
        if (!hbsTemplates[key]) {
          hbsTemplates[key] = Handlebars.compile(
              $("script[" + attrs.dataHbs + '="' + key + '"]').html()
          );
        }
        if (hbsTemplates[key]) {
          let cards = allCardTips; //_private.getMoreTipCards();
          elements[key] = $("#" + key);
          elements[key].html(
              hbsTemplates[key]({ cards: cards, isRegistered: logic.isRegistered })
          );
          _private.actions.bindClickOnDataTriggerBtn(elements[key]);
          elements[key] = elements[key].find("[" + attrs.dataCard + "]");
          elements[key].each(function (i) {
            if (i === 0) {
              _private.views.toggleCard(elements[key], i);
            }
          });
        }
      }
    };
    view.togglePrevTipBtn = function (cardPosition) {
      if (elements.previousBtnContainer.length) {
        elements.previousBtnContainer.addClass("d-none");
        let seeLikedTipsEle = elements.previousBtnContainer.find(
            "[" + attrs.dataTrigger + '="seeLikedTips"]'
        );
        let previousTipEle = elements.previousBtnContainer.find(
            "[" + attrs.dataTrigger + '="previousTip"]'
        );
        seeLikedTipsEle.addClass("d-none");
        previousTipEle.addClass("d-none");
        if (!isNaN(cardPosition)) {
          if (cardPosition >= 2) {
            elements.previousBtnContainer.removeClass("d-none");
            previousTipEle.removeClass("d-none");
          }
          if (_private.getNumUserCardsList() > 0) {
            elements.previousBtnContainer.removeClass("d-none");
            seeLikedTipsEle.removeClass("d-none");
          }
        }
        else if (_private.getNumUserCardsList() > 0) {
          elements.previousBtnContainer.removeClass("d-none");
          seeLikedTipsEle.removeClass("d-none");
        }
      }
    };
    view.toggleCard = function (ele, i) {
      ele.addClass("d-none");
      if (typeof i === "number") {
        $("[" + attrs.dataCard + '="' + i + '"]').removeClass("d-none");
      } else if (typeof i === "object") {
        i.removeClass("d-none");
      }
    };
    return view;
  })();

  _private.cookie = (function () {
    let o = {};
    o.getAllNameStartsWith = function (name) {
      let cookies = undefined;
      if (document.cookie) {
        let arr = document.cookie.trim().split("; ");
        for (let i in arr) {
          let arr2 = arr[i].trim().split("=");
          arr2[0] = arr2[0].trim();
          arr2[1] = arr2[1].trim();
          if (arr2[0].startsWith(name)) {
            if (cookies === undefined) {
              cookies = [];
            }
            cookies.push(arr2[1]);
          }
        }
      }
      return cookies;
    };
    o.exists = function (name, value) {
      if (value) {
        if (document.cookie) {
          let arr = document.cookie.trim().split("; ");
          for (let i in arr) {
            let arr2 = arr[i].trim().split("=");
            arr2[0] = arr2[0].trim();
            arr2[1] = arr2[1].trim();
            if (arr2[0].startsWith(name) && arr2[1] === value) {
              return arr2[0];
            }
          }
        }
        return false;
      }
      return o.get(name) !== undefined;
    };
    o.get = function (name) {
      let v = document.cookie.match("(^|;) ?" + name + "=([^;]*)(;|$)");
      return v && typeof v[2] !== "undefined" ? v[2] : undefined;
    };
    /**
     * @param obj is javascript object of
     * {name:"",value:"",expires:"(optional)",path:"(optional)",domain:"(optional)",secure:"(optional)"}
     */
    o.set = function (obj) {
      let name = obj.name || "";
      if (name) {
        let value = typeof obj.value !== "undefined" ? obj.value : undefined;
        if (typeof obj.days !== "undefined") {
          let d = new Date();
          d.setTime(d.getTime() + 24 * 60 * 60 * 1000 * obj.days);
          obj.expires = d.hasOwnProperty("toGMTString") ? d.toGMTString() : "";
        }
        document.cookie =
            name +
            "=" +
            value +
            (typeof obj.expires !== "undefined"
                ? "; expires=" + obj.expires
                : "") +
            ("; path=" + (obj.path || "/")) +
            (typeof obj.domain !== "undefined" ? "; domain=" + obj.domain : "") +
            (obj.hasOwnProperty("secure") && obj.secure ? "; secure" : "");
      }
    };
    o.delete = function (name) {
      document.cookie =
          name + "=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
    };
    return o;
  })();

  _private.getAllUserCurrentCards = function () {
    let cards = [];
    let currentUserCards = _private.cookie.getAllNameStartsWith(
        mindfulTipCookieName
    );
    if (currentUserCards) {
      for (let i = 0; i < allCardTips.length; i++) {
        let uuid = allCardTips[i].uuid;
        if (currentUserCards.indexOf(uuid) !== -1) {
          cards.push(allCardTips[i]);
        }
      }
    }
    return cards;
  };

  _private.getMoreTipCards = function () {
    let cards = [];
    let currentUserCards = _private.cookie.getAllNameStartsWith(
        mindfulTipCookieName
    );
    if (currentUserCards) {
      for (let i = 0; i < allCardTips.length; i++) {
        let uuid = allCardTips[i].uuid;
        if (currentUserCards.indexOf(uuid) === -1) {
          cards.push(allCardTips[i]);
        }
      }
    } else {
      cards = allCardTips;
    }
    return cards;
  };

  _private.getNumUserCardsList = function () {
    let cookies = _private.cookie.getAllNameStartsWith(mindfulTipCookieName);
    let i = 0;
    if (cookies) {
      for (let k in cookies) {
        i++;
      }
    }
    return i;
  };

  _private.getCurrentCardEle = function () {
    let e = null;
    if (elements.cardTips.length) {
      elements.cardTips.each(function () {
        let t = $(this);
        if (!t.hasClass("d-none")) {
          e = t;
        }
      });
    }
    return e;
  };

  _private.collapsible = function () {
    let tabs = $("#nav-tab a"),
        tabPanes = $("#nav-tabContent .tab-pane");
    if (tabs.length && tabPanes.length === tabs.length) {
      tabs.on("click", function (e) {
        e.preventDefault();
        let t = $(this),
            h = t.attr("href");
        tabPanes.removeClass("show").removeClass("active");
        $(h).addClass("show active");
        tabs.removeClass("active");
        t.addClass("active");
      });
    }
  };

  _private.randomCookieNameFromPfx = function (pfx) {
    let rand = Math.floor(Math.random() * 1000000);
    return pfx + parseInt("" + rand);
  };

  _private.beforeSend = function (xhr) {
    if (csrfToken) {
      xhr.setRequestHeader("CSRF-Token", csrfToken);
    }
  };

  _private.getCSRFToken = function (callbackOnSuccess) {
    $.ajax({
      url: csrfTokenPath,
      success:
          callbackOnSuccess ||
          function (resp) {
            if (resp && resp.token) {
              csrfToken = resp.token;
            }
          },
    });
  };

  _private.toJSONString = function(v) {return typeof v === "object" ? JSON.stringify(v) : "";};

  _private.toJSONObject = function(v) {return _private.isValidJSONString(v) ? JSON.parse(v) : {};};

  _private.isValidJSONString = function(text) {
    if (!text) {return false;}
    try {
      JSON.parse(text);
    }
    catch (e) {
      return false;
    }
    return true;
  };

  _private.setUserDataCookie = function() {
    _private.cookie.set({
      name: mindfulUserCookieName,
      value: _private.toJSONString(userData),
      days: cookieMaxLife
    });
  };

  _private.uri = function(){
    let o = {}, queryString = null;
    let arr = window.location.toString().split("?");

    o.getHost = function() {return location.host;};
    o.getHostname = function() {return location.hostname;};
    o.getPort = function() {return location.port;};
    o.getProtocol = function() {return location.protocol;};
    o.getHash = function() {return location.hash;};
    o.getFragment = function() {return o.getHash().replace('#!/', '/');};
    o.getPathname = function() {return location.pathname;};
    o.getHref = function() {return location.href;};
    o.getOrigin = function() {return location.origin;};
    o.getUrlPrefix = function() {return o.getProtocol()+'//'+o.getHost();};
    o.getPath = function() {
      return arr[0].replace(o.getUrlPrefix(), '')
          .replace('#!/'+o.getFragment(), '')
          .replace('#!'+o.getFragment(), '')
          .replace('#'+o.getFragment(), '');
    };

    o.getQueryString = function(key) {
      loadQueryString();
      if (arr.length > 1) {
        if (key) {
          return typeof queryString[key] !== "undefined" ? queryString[key] : "";
        }
        return decodeURI(arr[1].replace(o.getHash(), ''));
      }
      return '';
    };
    o.queryStringExists = function(key) {
      loadQueryString();
      return key && typeof queryString[key] !== "undefined";
    };
    o.getLangUri = function() {
      if (location.toString().indexOf('/language-master') !== -1) {
        return 'language-master';
      }
      return $('html').attr('lang');
    };
    o.formatUri = function(uri) {
      return uri.replace('language-master', o.getLangUri());
    };

    function loadQueryString() {
      if (arr.length > 1 && queryString === null) {
        let qStr = decodeURI(arr[1].replace(o.getHash(), ''));
        queryString = {};
        let qArr = qStr.split('&');
        for (let k in qArr) {
          let a = qArr[k].split('=');
          if (a.length === 2) {
            queryString[a[0].trim()] = a[1].trim();
          }
        }
      }
      else {
        queryString = {};
      }
    }

    return o;

  }();

  return _;
})(jQuery);

Mindful.init();