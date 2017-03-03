angular.module('orcidApp').factory("peerReviewSrvc", ['$rootScope', function ($rootScope) {
    var peerReviewSrvc = {
            constants: { 'access_type': { 'USER': 'user', 'ANONYMOUS': 'anonymous'}},
            groups: new Array(),            
            loading: false,
            loadingDetails: false,
            quickRef: {},            
            loadingDetails: false,
            blankPeerReview: null,
            details: new Object(), // we should think about putting details in the
            peerReviewsToAddIds: null,
            peerReviewGroupDetailsRequested: new Array(),
            getBlankPeerReview: function(callback) {
                 // if cached return clone of blank
                if (peerReviewSrvc.blankPeerReview != null)
                    callback(JSON.parse(JSON.stringify(peerReviewSrvc.blankPeerReview)));
                $.ajax({
                    url: getBaseUri() + '/peer-reviews/peer-review.json',
                    dataType: 'json',
                    success: function(data) {
                        callback(data);
                        $rootScope.$apply();
                    }
                }).fail(function() {
                    console.log("Error fetching blank Peer Review");
                });                
            },
            postPeerReview: function(peer_review, successFunc, failFunc) {              
                $.ajax({
                    url: getBaseUri() + '/peer-reviews/peer-review.json',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    type: 'POST',
                    data: angular.toJson(peer_review),
                    success: function(data) {
                        successFunc(data);
                    }
                }).fail(function(){
                    failFunc();
                });
            },
            createNew: function(peerReview) {
                var cloneF = JSON.parse(JSON.stringify(peerReview));
                cloneF.source = null;
                cloneF.putCode = null;
                for (var idx in cloneF.externalIdentifiers)
                    cloneF.externalIdentifiers[idx].putCode = null;
                return cloneF;
            },                   
            loadPeerReviews: function(access_type) {
                if (access_type == peerReviewSrvc.constants.access_type.ANONYMOUS) {                    
                    peerReviewSrvc.peerReviewsToAddIds = orcidVar.PeerReviewIds;
                    peerReviewSrvc.addPeerReviewsToScope(peerReviewSrvc.constants.access_type.ANONYMOUS);
                } else {
                    peerReviewSrvc.peerReviewsToAddIds = null;
                    peerReviewSrvc.loading = true;
                    peerReviewSrvc.groups = new Array();
                    peerReviewSrvc.details = new Object();
                    $.ajax({
                        url: getBaseUri() + '/peer-reviews/peer-review-ids.json',
                        dataType: 'json',
                        success: function(data) {
                            peerReviewSrvc.peerReviewsToAddIds = data;                          
                            peerReviewSrvc.addPeerReviewsToScope(peerReviewSrvc.constants.access_type.USER);
                            $rootScope.$apply();
                        }
                    }).fail(function(e){
                        // something bad is happening!
                        console.log("error fetching Peer Review");
                        logAjaxError(e);
                    });
                };
            },          
            addPeerReviewsToScope: function(type) {
                if (type == peerReviewSrvc.constants.access_type.USER)
                    var url = getBaseUri() + '/peer-reviews/get-peer-reviews.json?peerReviewIds=';
                else // use the anonymous url
                    var url = getBaseUri() + '/' + orcidVar.orcidId +'/peer-reviews.json?peerReviewIds=';
                if(peerReviewSrvc.peerReviewsToAddIds.length != 0 ) {
                    peerReviewSrvc.loading = true;
                    var peerReviewIds = peerReviewSrvc.peerReviewsToAddIds.splice(0,20).join();
                    $.ajax({
                        'url': url + peerReviewIds,
                        'dataType': 'json',
                        'success': function(data) {
                            $rootScope.$apply(function(){
                                for (i in data) {
                                    var dw = data[i];                                    
                                    removeBadExternalIdentifiers(dw);                                       
                                    groupedActivitiesUtil.group(dw,GroupedActivities.PEER_REVIEW,peerReviewSrvc.groups);
                                };
                            });
                            if(peerReviewSrvc.peerReviewsToAddIds.length == 0 ) {
                                peerReviewSrvc.loading = false;
                                $rootScope.$apply();
                            } else {
                                $rootScope.$apply();
                                setTimeout(function(){
                                    peerReviewSrvc.addPeerReviewsToScope(type);
                                },50);
                            }
                        }
                    }).fail(function(e) {
                        //$rootScope.$apply(function() {
                            peerReviewSrvc.loading = false;
                        //});
                        console.log("Error fetching Peer Review: " + peerReviewIds);
                        logAjaxError(e);
                    });
                } else {
                    peerReviewSrvc.loading = false;
                };
            },
            getGroup: function(putCode) {
                for (var idx in peerReviewSrvc.groups) {
                        if (peerReviewSrvc.groups[idx].hasPut(putCode))
                            return peerReviewSrvc.groups[idx];
                }
                return null;
            },
            getEditable: function(putCode, callback) {
                // first check if they are the current source
                var peerReview = peerReviewSrvc.getPeerReview(putCode);
                if (peerReview.source == orcidVar.orcidId)
                    callback(peerReview);
                else {
                    var bestMatch = null;
                    var group = peerReviewSrvc.getGroup(putCode);
                    for (var idx in group.activitiess) {
                        if (group[idx].source == orcidVar.orcidId) {
                            bestMatch = callback(group[idx]);
                            break;
                        }
                    }
                    if (bestMatch == null) 
                        bestMatch = peerReviewSrvc.createNew(peerReview);
                        callback(bestMatch);
                    };
            },
            getPeerReview: function(putCode) {
                for (var idx in peerReviewSrvc.groups) {
                        if (peerReviewSrvc.groups[idx].hasPut(putCode))
                            return peerReviewSrvc.groups[idx].getByPut(putCode);
                }
                return null;
            },
            deleteGroupPeerReview: function(putCodes) {
                var rmPeerReview = new Array();
                var rmGroups = new Array();
                for (var idj in putCodes)
                    for (var idx in peerReviewSrvc.groups) {
                        if (peerReviewSrvc.groups[idx].hasPut(putCodes[idj])) {
                            rmGroups.push(idx);
                            for (var idj in peerReviewSrvc.groups[idx].activities)
                                rmPeerReview.push(peerReviewSrvc.groups[idx].activities[idj].putCode.value);
                        };
                    }
                while (rmGroups.length > 0) 
                    peerReviewSrvc.groups.splice(rmGroups.pop(),1);
                peerReviewSrvc.removePeerReview(rmPeerReview);
            },
            deletePeerReview: function(putCode) {
                peerReviewSrvc.removePeerReview([putCode], function() {peerReviewSrvc.loadPeerReviews(peerReviewSrvc.constants.access_type.USER);});
            },
            makeDefault: function(group, putCode) {
                group.makeDefault(putCode);
                $.ajax({
                    url: getBaseUri() + '/peer-reviews/updateToMaxDisplay.json?putCode=' + putCode,
                    type: 'GET',
                    dataType: 'json',
                    success: function(data) {
                    }
                }).fail(function(){
                    // something bad is happening!
                    console.log("Error: peerReviewSrvc.makeDefault method");
                });
            },
            removePeerReview: function(putCodes,callback) {
                $.ajax({
                    url: getBaseUri() + '/peer-reviews/' + putCodes.splice(0,150).join(),
                    type: 'DELETE',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        if (putCodes.length > 0) 
                            peerReviewSrvc.removePeerReview(putCodes,callback);
                        else if (callback)
                            callback(data);
                    }
                }).fail(function() {
                    console.log("Error deleting Peer Review.");
                });
            },
            setGroupPrivacy: function(putCode, priv) {
                var group = peerReviewSrvc.getGroup(putCode);
                var putCodes = new Array();
                for (var idx in group.activities) {
                    putCodes.push(group.activities[idx].putCode.value);
                    group.activities[idx].visibility = priv;
                }
                peerReviewSrvc.updateVisibility(putCodes, priv);
            },
            setPrivacy: function(putCode, priv) {
                peerReviewSrvc.updateVisibility([putCode], priv);
            },
            updateVisibility: function(putCodes, priv) {
                $.ajax({
                    url: getBaseUri() + '/peer-reviews/' + putCodes.splice(0,150).join() + '/visibility/'+priv.toLowerCase(),
                    type: 'GET',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        if (putCodes.length > 0)
                            peerReviewSrvc.updateVisibility(putCodes, priv);
                    }
                }).fail(function() {
                    console.log("Error updating profile Peer Review.");
                });
            },
            peerReviewCount: function() {
                var count = 0;
                for (var idx in peerReviewSrvc.groups) {
                    count += peerReviewSrvc.groups[idx].activitiesCount;
                }
                return count;
            },
            getPeerReviewGroupDetails: function(groupIDPutCode, putCode){
                if(groupIDPutCode != undefined) {
                    if (peerReviewSrvc.peerReviewGroupDetailsRequested.indexOf(groupIDPutCode) < 0){                    
                        peerReviewSrvc.peerReviewGroupDetailsRequested.push(groupIDPutCode);                    
                        var group = peerReviewSrvc.getGroup(putCode);
                        $.ajax({
                            url: getBaseUri() + '/public/group/' + groupIDPutCode,
                            dataType: 'json',
                            contentType: 'application/json;charset=UTF-8',
                            type: 'GET',
                            success: function(data) {
                                $rootScope.$apply(function(){
                                    console.log(angular.toJson(data));
                                    group.groupName = data.name;
                                    group.groupDescription = data.description;
                                    group.groupType = data.type;
                                });
                            }
                        }).fail(function(xhr, status, error){
                            console.log("Error: " + status + "\nError: " + error + "\nError detail: " + xhr.responseText);
                        });
                        
                    }
                } else {
                    console.log("Error: undefined group id for peer review with put code: " + putCode);  
                }       
            }
    };
    return peerReviewSrvc;
}]);