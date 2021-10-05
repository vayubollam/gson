var data= FR;

window.onload = function () {

    // (Panel-1) Article Rewards-Discover
    document.getElementById("rewards-discover-title").innerHTML = data.rewardDiscover.title;
    document.getElementById("rewards-discover-description").innerHTML = data.rewardDiscover.description;
    document.getElementById("rewards-discover-image").src="images/panel-1/top.jpg";

    // (Panel-2) Article Rewards-Dollar-to-Points
    document.getElementById("rewards-dollar-to-points-title").innerHTML = data.rewardsDollarToPoints.title;
    document.getElementById("rewards-dollar-to-points-image").src="images/panel-2/dollars-litres-fr.gif";
    
    // (Panel-3) Article Rewards-Redeem
    document.getElementById("rewards-redeem-tip").innerHTML = data.rewardsRedeem.note;
    document.getElementById("rewards-redeem-description").innerHTML = data.rewardsRedeem.description;

    // (Panel-3a) Article Rewards-Image 
    document.getElementById("rewards-image-image").src="images/panel-3a/station.jpg";

    // (Panel-4) Article Rewards-In-Store-Purchase
    document.getElementById("rewards-in-store-purchase-title").innerHTML = data.rewardsInStorePurchase.title;

    document.getElementById("rewards-in-store-purchase-ticket-image").src="images/panel-4/card-cwticket.png";
    document.getElementById("rewards-in-store-purchase-ticket-title").innerHTML = data.rewardsInStorePurchase.ticket.title;
    document.getElementById("rewards-in-store-purchase-ticket-description").innerHTML = data.rewardsInStorePurchase.ticket.description;

    document.getElementById("rewards-in-store-purchase-fsr-image").src="images/panel-4/card-fsr.png";
    document.getElementById("rewards-in-store-purchase-fsr-title").innerHTML = data.rewardsInStorePurchase.fsr.title;
    document.getElementById("rewards-in-store-purchase-fsr-description").innerHTML = data.rewardsInStorePurchase.fsr.description;

    document.getElementById("rewards-in-store-purchase-travel-image").src="images/panel-4/card-travel.png";
    document.getElementById("rewards-in-store-purchase-travel-title").innerHTML = data.rewardsInStorePurchase.travel.title;
    document.getElementById("rewards-in-store-purchase-travel-description").innerHTML = data.rewardsInStorePurchase.travel.description;

    document.getElementById("rewards-in-store-purchase-wag-image").src="images/panel-4/card-wag.png";
    document.getElementById("rewards-in-store-purchase-wag-title").innerHTML = data.rewardsInStorePurchase.washAndGo.title;
    document.getElementById("rewards-in-store-purchase-wag-description").innerHTML = data.rewardsInStorePurchase.washAndGo.description;

    document.getElementById("rewards-in-store-purchase-sp-image").src="images/panel-4/card-sp.png";
    document.getElementById("rewards-in-store-purchase-sp-title").innerHTML = data.rewardsInStorePurchase.seasonPass.title;
    document.getElementById("rewards-in-store-purchase-sp-description").innerHTML = data.rewardsInStorePurchase.seasonPass.description;

    // (Panel-5) Article Rewards-EGift 
    document.getElementById("rewards-egift-title").innerHTML = data.rewardsEgift.title;

    document.getElementById("rewards-egift-hbc-image").src="images/panel-5/card-ehbc.png";
    document.getElementById("rewards-egift-hbc-title").innerHTML = data.rewardsEgift.hbc.title;
    document.getElementById("rewards-egift-hbc-description").innerHTML = data.rewardsEgift.hbc.description;

    document.getElementById("rewards-egift-ultimate-image").src="images/panel-5/card-eultimatedining.png";
    document.getElementById("rewards-egift-ultimate-title").innerHTML = data.rewardsEgift.ultimateDinning.title;
    document.getElementById("rewards-egift-ultimate-description").innerHTML = data.rewardsEgift.ultimateDinning.description;

    document.getElementById("rewards-egift-retail-image").src="images/panel-5/card-ewinners.png";
    document.getElementById("rewards-egift-retail-title").innerHTML = data.rewardsEgift.winnersMarshallsHomeSense.title;
    document.getElementById("rewards-egift-retail-description").innerHTML = data.rewardsEgift.winnersMarshallsHomeSense.description;

    document.getElementById("rewards-egift-cineplex-image").src="images/panel-5/card-ecineplex.png";
    document.getElementById("rewards-egift-cineplex-title").innerHTML = data.rewardsEgift.cineplex.title;
    document.getElementById("rewards-egift-cineplex-description").innerHTML = data.rewardsEgift.cineplex.description;

    // (Panel-6) Article Rewards-Link
    document.getElementById("rewards-link-title").innerHTML = data.rewardsLink.title
    document.getElementById("rewards-link-description").innerHTML = data.rewardsLink.description;

    document.getElementById("rewards-link-rbc-image").src="images/panel-6/card-rbc.png";
    document.getElementById("rewards-link-rbc-title").innerHTML = data.rewardsLink.rbc.title
    document.getElementById("rewards-link-rbc-description").innerHTML = data.rewardsLink.rbc.description;

    document.getElementById("rewards-link-hbc-image").src="images/panel-6/HBC_REWARDS_MCARDS_FR.png";
    document.getElementById("rewards-link-hbc-title").innerHTML = data.rewardsLink.hbc.title
    document.getElementById("rewards-link-hbc-description").innerHTML = data.rewardsLink.hbc.description;

//    document.getElementById("rewards-link-caa-image").src="images/panel-6/card-caa.png";
//    document.getElementById("rewards-link-caa-title").innerHTML = data.rewardsLink.caa.title
//    document.getElementById("rewards-link-caa-description").innerHTML = data.rewardsLink.caa.description;
//
//    document.getElementById("rewards-link-bcaa-image").src="images/panel-6/card-bcaa.png";
//    document.getElementById("rewards-link-bcaa-title").innerHTML = data.rewardsLink.bcaa.title
//    document.getElementById("rewards-link-bcaa-description").innerHTML = data.rewardsLink.bcaa.description;

//    document.getElementById("rewards-link-more-image").src="images/panel-6/card-morerewards.png";
//    document.getElementById("rewards-link-more-title").innerHTML = data.rewardsLink.more.title
//    document.getElementById("rewards-link-more-description").innerHTML = data.rewardsLink.more.description;

    // (Panel-7) Article Rewards-Partners 
    document.getElementById("rewards-partners-title").innerHTML = data.rewardsPartners.title
    document.getElementById("rewards-partners-partner-1-description").innerHTML = data.rewardsPartners.partner1.description;
//    document.getElementById("rewards-partners-partner-2-description").innerHTML = data.rewardsPartners.partner2.description;
    document.getElementById("rewards-partners-partner-3-description").innerHTML = data.rewardsPartners.partner3.description;
    

    document.getElementById("rewards-partners-partner-1-image").src="images/panel-7/card-askingcdns.png";
//    document.getElementById("rewards-partners-partner-2-image").src="images/panel-7/card-carrot.png";
    document.getElementById("rewards-partners-partner-3-image").src="images/panel-7/card-itravel2000.png";

    // (Panel-8) Article Rewards-Exchange
//    document.getElementById("rewards-exchange-title").innerHTML = data.rewardsExchange.title
//    document.getElementById("rewards-exchange-description").innerHTML = data.rewardsExchange.description;
//    document.getElementById("rewards-exchange-image").src="images/panel-8/exchange-fr.gif";

    // (Panel-9) Article Rewards-Join
    document.getElementById("rewards-join-title").innerHTML = data.rewardsJoin.title;
    document.getElementById("rewards-join-description").innerHTML = data.rewardsJoin.description;
    document.getElementById("rewards-join-image").src="images/panel-9/bottom.jpg";
 }