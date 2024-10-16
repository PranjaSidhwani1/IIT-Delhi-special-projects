// package main

// import (
// 	"fmt"
// 	"gomls/pkg/helpers"
// 	"gomls/pkg/zillow"
// )

// func main() {
// 	details := helpers.Details{
// 		Location:     "93644",
// 		Price:        50000,
// 		Baths:        0,
// 		Beds:         0,
// 		PropertyType: "",
// 		Sold:         false,
// 	}

// 	houses := zillow.Query(details)
// 	fmt.Println(houses)
// }

package main

import (
	"encoding/csv"
	"fmt"
	"gomls/pkg/helpers"
	"gomls/pkg/zillow"
	"os"
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

	err := writeCSV("output.csv", houses)
	if err != nil {
		fmt.Printf("Error writing CSV: %v\n", err)
		return
	}

	fmt.Println("CSV file generated successfully.")
}

func writeCSV(filename string, houses []helpers.House) error {
	file, err := os.Create(filename)
	if err != nil {
		return err
	}
	defer file.Close()

	writer := csv.NewWriter(file)
	defer writer.Flush()

	// Write CSV header
	header := []string{"Address", "ListPrice", "Bedrooms", "Bathrooms", "HomeType", "URL"}
	writer.Write(header)

	// Write data
	for _, house := range houses {
		row := []string{
			house.Property.Address,
			fmt.Sprintf("%d", house.Property.ListPrice),
			fmt.Sprintf("%d", house.Property.ResoFacts.Bedrooms),
			fmt.Sprintf("%d", house.Property.ResoFacts.Bathrooms),
			house.Property.HomeType,
			house.Property.FullUrl,
		}
		writer.Write(row)
	}

	return nil
}
