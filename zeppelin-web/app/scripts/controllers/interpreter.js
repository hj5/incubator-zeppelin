/* global confirm:false, alert:false */
/* jshint loopfunc: true */
/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';

/**
 * @ngdoc function
 * @name zeppelinWebApp.controller:InterpreterCtrl
 * @description
 * # InterpreterCtrl
 * Controller of interpreter, manage the note (update)
 */
angular.module('zeppelinWebApp').controller('InterpreterCtrl', function($scope, $route, $routeParams, $location, $rootScope, $http) {

  var remoteSettingToLocalSetting = function(settingId, setting) {
    var property = {};
    for (var key in setting.properties) {
      property[key] = {
        value : setting.properties[key]
      };
    }
    return {
      id : settingId,
      name : setting.name,
      group : setting.group,
      option : angular.copy(setting.option),
      properties : property,
      interpreters : setting.interpreterGroup
    };
  };

  var getInterpreterSettings = function() {
    $http.get(getRestApiBase()+'/interpreter/setting').
      success(function(data, status, headers, config) {
        var interpreterSettings = [];
        //console.log("getInterpreterSettings=%o", data);

        for (var settingId in data.body) {
          var setting = data.body[settingId];
          interpreterSettings.push(remoteSettingToLocalSetting(setting.id, setting));
        }
        $scope.interpreterSettings = interpreterSettings;
      }).
      error(function(data, status, headers, config) {
        console.log('Error %o %o', status, data.message);
      });
  };

  var getAvailableInterpreters = function() {
    $http.get(getRestApiBase()+'/interpreter').
      success(function(data, status, headers, config) {
        var groupedInfo = {};
        var info;
        for (var k in data.body) {
          info = data.body[k];
          if (!groupedInfo[info.group]) {
            groupedInfo[info.group] = [];
          }
          groupedInfo[info.group].push({
            name : info.name,
            className : info.className,
            properties : info.properties
          });
        }

        $scope.availableInterpreters = groupedInfo;
        //console.log("getAvailableInterpreters=%o", data);
      }).
      error(function(data, status, headers, config) {
        console.log('Error %o %o', status, data.message);
      });
  };

  $scope.copyOriginInterpreterSettingProperties = function(settingId) {
    $scope.interpreterSettingProperties = {};
    for (var i=0; i < $scope.interpreterSettings.length; i++) {
      var setting = $scope.interpreterSettings[i];
      if(setting.id === settingId) {
        angular.copy(setting.properties, $scope.interpreterSettingProperties);
        angular.copy(setting.option, $scope.interpreterSettingOption);
        break;
      }
    }
    console.log('%o, %o', $scope.interpreterSettings[i], $scope.interpreterSettingProperties);
  };

  $scope.updateInterpreterSetting = function(settingId) {
    var result = confirm('\u4f60\u786e\u5b9a\u60f3\u8981\u66f4\u65b0\u8fd9\u4e2a\u89e3\u91ca\u5668\u8bbe\u7f6e\u5e76\u91cd\u542f\u8be5\u89e3\u91ca\u5668\u5417\u003f');
    if (!result) {
      return;
    }

    $scope.addNewInterpreterProperty(settingId);

    var request = {
      option : {
        remote : true
      },
      properties : {},
    };

    for (var i=0; i < $scope.interpreterSettings.length; i++) {
      var setting = $scope.interpreterSettings[i];
      if(setting.id === settingId) {
        request.option = angular.copy(setting.option);
        for (var p in setting.properties) {
          request.properties[p] = setting.properties[p].value;
        }
        break;
      }
    }

    $http.put(getRestApiBase()+'/interpreter/setting/'+settingId, request).
    success(function(data, status, headers, config) {
      for (var i=0; i < $scope.interpreterSettings.length; i++) {
        var setting = $scope.interpreterSettings[i];
        if (setting.id === settingId) {
          $scope.interpreterSettings.splice(i, 1);
          $scope.interpreterSettings.splice(i, 0, remoteSettingToLocalSetting(settingId, data.body));
          break;
        }
      }
    }).
    error(function(data, status, headers, config) {
      console.log('Error %o %o', status, data.message);
    });
  };

  $scope.resetInterpreterSetting = function(settingId){
    for (var i=0; i<$scope.interpreterSettings.length; i++) {
      var setting = $scope.interpreterSettings[i];
      if (setting.id === settingId) {
        angular.copy($scope.interpreterSettingProperties, setting.properties);
        angular.copy($scope.interpreterSettingOption, setting.option);
        break;
      }
    }
  };

  $scope.removeInterpreterSetting = function(settingId) {
    var result = confirm('\u4f60\u786e\u5b9a\u60f3\u8981\u5220\u9664\u8fd9\u4e2a\u89e3\u91ca\u5668\u8bbe\u7f6e\u5417\u003f');
    if (!result) {
      return;
    }

    console.log('Delete setting %o', settingId);
    $http.delete(getRestApiBase()+'/interpreter/setting/'+settingId).
      success(function(data, status, headers, config) {
        for (var i=0; i < $scope.interpreterSettings.length; i++) {
          var setting = $scope.interpreterSettings[i];
          if (setting.id === settingId) {
            $scope.interpreterSettings.splice(i, 1);
            break;
          }
        }
      }).
      error(function(data, status, headers, config) {
        console.log('Error %o %o', status, data.message);
      });
  };

  $scope.newInterpreterGroupChange = function() {
    var property = {};
    var intpGroupInfo = $scope.availableInterpreters[$scope.newInterpreterSetting.group];
    for (var i=0; i<intpGroupInfo.length; i++) {
      var intpInfo = intpGroupInfo[i];
      for (var key in intpInfo.properties) {
        property[key] = {
          value : intpInfo.properties[key].defaultValue,
          description : intpInfo.properties[key].description
        };
      }
    }
    $scope.newInterpreterSetting.properties = property;
  };

  $scope.restartInterpreterSetting = function(settingId) {
    var result = confirm('\u4f60\u786e\u5b9a\u60f3\u8981\u91cd\u542f\u8fd9\u4e2a\u89e3\u91ca\u5668\u8bbe\u7f6e\u5417\u003f');
    if (!result) {
      return;
    }

    $http.put(getRestApiBase()+'/interpreter/setting/restart/'+settingId).
      success(function(data, status, headers, config) {
        for (var i=0; i < $scope.interpreterSettings.length; i++) {
          var setting = $scope.interpreterSettings[i];
          if (setting.id === settingId) {
            $scope.interpreterSettings.splice(i, 1);
            $scope.interpreterSettings.splice(i, 0, remoteSettingToLocalSetting(settingId, data.body));
            break;
          }
        }
      }).
      error(function(data, status, headers, config) {
        console.log('Error %o %o', status, data.message);
      });
  };

  $scope.addNewInterpreterSetting = function() {
    if (!$scope.newInterpreterSetting.name || !$scope.newInterpreterSetting.group) {
      alert('\u8bf7\u8f93\u5165\u540d\u79f0\u548c\u89e3\u91ca\u5668\u7c7b\u578b');
      return;
    }

    for (var i=0; i<$scope.interpreterSettings.length; i++) {
      var setting = $scope.interpreterSettings[i];
      if (setting.name === $scope.newInterpreterSetting.name) {
        alert('\u540d\u79f0 ' + setting.name + ' \u5df2\u7ecf\u5b58\u5728');
        return;
      }
    }

    $scope.addNewInterpreterProperty();

    var newSetting = {
      name : $scope.newInterpreterSetting.name,
      group : $scope.newInterpreterSetting.group,
      option : angular.copy($scope.newInterpreterSetting.option),
      properties : {}
    };

    for (var p in $scope.newInterpreterSetting.properties) {
      newSetting.properties[p] = $scope.newInterpreterSetting.properties[p].value;
    }

    $http.post(getRestApiBase()+'/interpreter/setting', newSetting).
      success(function(data, status, headers, config) {
        $scope.resetNewInterpreterSetting();
        getInterpreterSettings();
        $scope.showAddNewSetting = false;
      }).
      error(function(data, status, headers, config) {
        console.log('Error %o %o', status, data.message);
      });
  };


  $scope.resetNewInterpreterSetting = function() {
    $scope.newInterpreterSetting = {
      name : undefined,
      group : undefined,
      option : { remote : true },
      properties : {}
    };
    $scope.newInterpreterSetting.propertyValue = '';
    $scope.newInterpreterSetting.propertyKey = '';
  };

  $scope.removeInterpreterProperty = function(key, settingId) {
    if (settingId === undefined) {
      delete $scope.newInterpreterSetting.properties[key];
    }
    else {
      for (var i=0; i < $scope.interpreterSettings.length; i++) {
        var setting = $scope.interpreterSettings[i];
        if (setting.id === settingId) {
          delete $scope.interpreterSettings[i].properties[key]
          break;
        }
      }
    }
  };

  $scope.addNewInterpreterProperty = function(settingId) {
    if(settingId === undefined) {
      if (!$scope.newInterpreterSetting.propertyKey || $scope.newInterpreterSetting.propertyKey === '') {
        return;
      }
      $scope.newInterpreterSetting.properties[$scope.newInterpreterSetting.propertyKey] = { value : $scope.newInterpreterSetting.propertyValue};
      $scope.newInterpreterSetting.propertyValue = '';
      $scope.newInterpreterSetting.propertyKey = '';
    }
    else {
      for (var i=0; i < $scope.interpreterSettings.length; i++) {
        var setting = $scope.interpreterSettings[i];
        if (setting.id === settingId){
          if (!setting.propertyKey || setting.propertyKey === '') {
            return;
          }
          setting.properties[setting.propertyKey] = { value : setting.propertyValue };
          setting.propertyValue = '';
          setting.propertyKey = '';
          break;
        }
      }
    }
  };

  var init = function() {
    // when interpreter page opened after seeing non-default looknfeel note, the css remains unchanged. that's what interpreter page want. Force set default looknfeel.
    $rootScope.$emit('setLookAndFeel', ['default','\u9ed8\u8ba4\u6a21\u5f0f']);
    $scope.interpreterSettings = [];
    $scope.availableInterpreters = {};
    $scope.resetNewInterpreterSetting();

    getInterpreterSettings();
    getAvailableInterpreters();
  };

  init();
});
