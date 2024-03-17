package com.alphadominche.steampunkhmi;

public enum SPServiceCrucibleState { //given in chronological order assuming coffee is being brewed
	EMPTY_START, //the crucible is empty and ready to start brewing a beverage
	FILLING_FOR_BREW, //water is being put into the crucible for the sake of brewing a beverage
	PUSHING_BREW_WATER_TO_TOP, //prescribed amount of water is in crucible, lasts for 5 seconds
	COMPENSATING_VOLUME_FOR_STEAM,
	MAKE_SURE_BREW_WATER_IS_IN_TOP,
	BREW_WATER_IN_TOP_AND_HEATING, //until temperature is at target
	START_BREW_DRAIN, //water is in top and the drain actuator gets opened while the steam is still opened to drain to the bottom, lasts 1/3 of a second
	FINISH_BREW_DRAIN, //drain actuator held open for 7 more seconds while steam is turned off
	WAITING_FOR_BREW_PISTON_INSERTION, //effectively on idle until the operator hits the brew button
	AGITATING, //1/2 second of steam, 1/2 second of no steam
	STEEP_BETWEEN_AGITATIONS, //idling while waiting for the next agitation or the draining of the beverage
	BEVERAGE_VACUUM_BREAK, //holding the steam open for a bit to allow the drain actuator to open (compensating for vacuum caused by cooling in the bottom of the crucible)
	START_BEVERAGE_PULL_DOWN, //holding steam and drain actuator open for 1/3 of a second
	FINISH_BEVERAGE_PULL_DOWN, //turning off steam while holding the drain actuator open
	WAITING_TO_DISPENSE_AND_RINSE, //the beverage is read for the operator to drain into a cup to give to the consumer...should probably prompt operator to remove piston at this point
	FILLING_FOR_RINSE, //water is being put into the crucible for the sake of rinsing
	PUSHING_RINSE_WATER_TO_TOP, //prescribed amount of water is in crucible, lasts for 5 seconds
	RINSE_WATER_IN_TOP_AND_HEATING, //steam is held open to heat the water and agitate it enough to rinse the crucible
	START_RINSE_DRAIN, //steam and drain are both held open for 1/3 second
	FINISH_RINSE_DRAIN, //drain actuator held open for 7 more seconds while steam is turned off...effective end of a brew cycle!
	FILL_FOR_CLEAN, //water is being put into the crucible for the sake of washing it
	HEAT_FOR_CLEAN, //water is being heated to the correct cleaning temperature, at least 5 seconds to include pushing water up to the top
	AGITATE_FOR_CLEAN, //water is being agitated for the cleaning action
	SIT_WITH_WATER_AT_TOP_FOR_CLEAN, //let the soapy water soak in the top for a bit
	CLEAN_VACUUM_BREAK, //should be 3 seconds
	START_CLEAN_DRAIN, //1/3 second overlap
	WAITING_FOR_CLEAN_DRAIN, //drain the soapy water
	SIT_WITH_WATER_AT_BOTTOM_FOR_CLEAN, //let the soapy water soak in the bottom for a bit
	WAIT_FOR_CLEAN_RINSE, //user can hit the crucible here to start the rinse cycle
	FILL_FOR_CLEAN_RINSE, //water is being put into the crucible for the sake of rinsing out the soapy water
	HEAT_FOR_CLEAN_RINSE, //water is being heated to the correct cleaning temperature, at least 5 seconds
	AGITATE_FOR_CLEAN_RINSE, //water is being agitated for rinsing action
	SIT_WITH_WATER_AT_TOP_FOR_CLEAN_RINSE, //let the rinse water soak in the top for a bit
	CLEAN_RINSE_VACUUM_BREAK, //should be 3 seconds
	START_CLEAN_RINSE_DRAIN, //1/3 second overlap
	WAITING_FOR_CLEAN_RINSE_DRAIN, //drain the rinse water
	SIT_WITH_WATER_AT_BOTTOM_FOR_CLEAN_RINSE //let the rinse water soak in the bottom for a bit
}
