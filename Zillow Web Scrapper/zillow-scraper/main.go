package main

import (
	"fmt"
	"gomls/pkg/helpers"
	"gomls/pkg/zillow"
)

func main() {
	details := helpers.Details{
		Location:     "93644",
		Price:        50000,
		Baths:        0,
		Beds:         0,
		PropertyType: "",
		Sold:         false,
	}
	houses := zillow.Query(details)
	fmt.Println(houses)
}
