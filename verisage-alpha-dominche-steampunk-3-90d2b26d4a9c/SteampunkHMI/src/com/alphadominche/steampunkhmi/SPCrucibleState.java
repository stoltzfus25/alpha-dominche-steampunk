package com.alphadominche.steampunkhmi;

enum SPCrucibleState {
	IDLE,
	FILLING,
	HEATING,
	INSERT_PISTON,
	START_BREWING,
	AGITATING,
	STEEPING,
	EXTRACTING,
	START_RINSING,
	RINSING,
	CLEANING_FILL_AND_HEAT,
	CLEANING_DRAIN,
	CLEANING_AGITATING,
	CLEANING_SOAK,
	WAITING_FOR_CLEANING_RINSE,
	CLEANING_RINSE_FILL_AND_HEAT,
	CLEANING_RINSE_DRAIN,
	CLEANING_RINSE_AGITATING,
	CLEANING_RINSE_SOAK,
	WAITING_FOR_NEXT_STACK,
	DISABLED
}
