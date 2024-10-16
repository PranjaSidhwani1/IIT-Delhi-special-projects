// // Package zillow scrapes real estate listings from a zillow.com
// package zillow

// import (
// 	"fmt"
// 	"gomls/pkg/helpers"
// 	"strings"
// 	"time"

// 	"github.com/gocolly/colly"
// )

// const (
// 	base = "https://www.zillow.com"
// )

// var (
// 	houses = []helpers.House{}
// 	c      = colly.NewCollector(
// 		colly.MaxDepth(1),
// 		colly.Async(true),
// 	)
// )

// func init() {
// 	c.OnError(func(r *colly.Response, err error) {
// 		fmt.Printf("Error visiting %s %s\n", r.Request.URL, err.Error())
// 	})
// 	c.Limit(&colly.LimitRule{
// 		Parallelism: 2,
// 		RandomDelay: 20 * time.Second,
// 	})

// 	c.OnRequest(func(r *colly.Request) {
// 		if strings.Contains(r.URL.String(), `/homes/`) {
// 			fmt.Printf("Extracting property links from %q\n", r.URL)
// 		} else {
// 			fmt.Printf("--> Extracting property data from %q \n", r.URL)
// 		}

// 		// https://www.scrapehero.com/how-to-scrape-real-estate-listings-on-zillow-com-using-python-and-lxml/
// 		r.Headers.Set("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
// 		r.Headers.Set("accept-language", "en-GB;q=0.9,en-US;q=0.8,en;q=0.7")
// 		r.Headers.Set("dpr", "1")
// 		r.Headers.Set("sec-fetch-dest", "document")
// 		r.Headers.Set("sec-fetch-mode", "navigate")
// 		r.Headers.Set("sec-fetch-site", "none")
// 		r.Headers.Set("sec-fetch-user", "?1")
// 		r.Headers.Set("upgrade-insecure-requests", "1")
// 		r.Headers.Set("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36")
// 		r.Headers.Set("usePrimedCacheWhenDisabled", "true")
// 	})
// }

// func newDetails(d helpers.Details, h helpers.House) *helpers.House {
// 	url := h.Property.HdpUrl
// 	fullAddress := strings.SplitN(url, "/", -1)[2]
// 	h.Property.Address = strings.ReplaceAll(fullAddress, "-", " ")
// 	h.Property.MapsUrl = fmt.Sprintf("https://maps.google.com/?q=%s", fullAddress)
// 	h.Property.FullUrl = fmt.Sprintf("%s%s", base, url)

// 	for k, v := range h.Property.PriceHistory {
// 		if v.Event == "Listed for sale" && h.Property.ListPrice == 0 {
// 			h.Property.ListPrice = v.Price
// 			h.Property.ListDate = v.Date
// 			h.Property.PriceHistory = h.Property.PriceHistory[k:]
// 		}

// 		// Properties listed for sale aren't sold...
// 		if d.Sold {
// 			if v.Event == "Sold" && h.Property.SoldPrice == 0 {
// 				h.Property.SoldPrice = v.Price
// 				h.Property.SoldDate = v.Date
// 				h.Property.PriceHistory = h.Property.PriceHistory[k:]
// 			}

// 			if h.Property.ListPrice != 0 && h.Property.SoldPrice != 0 {
// 				h.Property.PriceDiff = h.Property.SoldPrice - h.Property.ListPrice
// 				h.Property.PriceDiffPercent = int(float64(h.Property.SoldPrice) / float64(h.Property.ListPrice) * 100)
// 			}
// 		}
// 	}

// 	return &h
// }

// func filter(d helpers.Details, h helpers.House) bool {
// 	if d.Baths > h.Property.ResoFacts.Bathrooms {
// 		return false
// 	}

// 	if d.Beds > h.Property.ResoFacts.Bedrooms {
// 		return false
// 	}

// 	if d.PropertyType != "" && d.PropertyType != h.Property.HomeType {
// 		return false
// 	}

// 	if h.Property.SoldPrice != 0 && d.Price > h.Property.SoldPrice {
// 		return false
// 	}

// 	if h.Property.ListPrice != 0 && d.Price > h.Property.ListPrice {
// 		return false
// 	}
// 	return true
// }

// // Query that scrapes real estate listings from a website
// func Query(d helpers.Details) helpers.HouseSlice {
// 	status := "for_sale/"
// 	if d.Sold {
// 		status = "recently_sold/"
// 	}
// 	// Either:
// 	// - https://www.zillow.com/homes/for_sale/<location>_rb/
// 	// - https://www.zillow.com/homes/recently_sold/<location>_rb/
// 	url := fmt.Sprintf(`%s/homes/%s%s_rb/`, base, status, d.Location)

// 	// This class is unique to the div that holds all information about a house
// 	// This filter excludes "Similar results nearby"
// 	c.OnHTML("ul[class*='photo-cards_extra-attribution']", func(e *colly.HTMLElement) {
// 		e.ForEach("a[data-test='property-card-link']", func(i int, houseElement *colly.HTMLElement) {
// 			pURL := houseElement.Attr("href")
// 			c.Visit(pURL)
// 		})
// 	})

// 	// __NEXT_DATA__ is a JSON-like object that contains metadata about the property
// 	c.OnHTML("script#__NEXT_DATA__", func(e *colly.HTMLElement) {
// 		// Only grab data from the property pages, ignore search results page
// 		if !strings.Contains(e.Request.URL.Path, "homedetails") {
// 			return
// 		}

// 		// Convert the string to a JSON object
// 		j := helpers.StringToJSON(e.Text)

// 		// Initialize values in the House struct
// 		h := newDetails(d, *j)

// 		// House doesn't match filters, return early
// 		if !filter(d, *h) {
// 			return
// 		}

// 		houses = append(houses, *h)
// 	})

// 	c.Visit(url)
// 	c.Wait()

// 	if len(houses) == 0 {
// 		fmt.Println("Property search returned no results")
// 	}
// 	return houses
// }

package zillow

import (
	"fmt"
	"gomls/pkg/helpers"
	"strings"
	"time"

	"github.com/gocolly/colly"
)

// Constants
const (
	base = "https://www.zillow.com"
)

var (
	houses = []helpers.House{}
	c      = colly.NewCollector(
		colly.MaxDepth(1),
		colly.Async(true),
	)
)

func init() {
	c.OnError(func(r *colly.Response, err error) {
		fmt.Printf("Error visiting %s %s\n", r.Request.URL, err.Error())
	})
	c.Limit(&colly.LimitRule{
		Parallelism: 2,
		RandomDelay: 20 * time.Second,
	})

	c.OnRequest(func(r *colly.Request) {
		if strings.Contains(r.URL.String(), `/homes/`) {
			fmt.Printf("Extracting property links from %q\n", r.URL)
		} else {
			fmt.Printf("--> Extracting property data from %q \n", r.URL)
		}

		// Set headers
		r.Headers.Set("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
		r.Headers.Set("accept-language", "en-GB;q=0.9,en-US;q=0.8,en;q=0.7")
		r.Headers.Set("dpr", "1")
		r.Headers.Set("sec-fetch-dest", "document")
		r.Headers.Set("sec-fetch-mode", "navigate")
		r.Headers.Set("sec-fetch-site", "none")
		r.Headers.Set("sec-fetch-user", "?1")
		r.Headers.Set("upgrade-insecure-requests", "1")
		r.Headers.Set("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36")
		r.Headers.Set("usePrimedCacheWhenDisabled", "true")
	})
}

// newDetails creates new House details based on the parsed data
func newDetails(d helpers.Details, h helpers.House) *helpers.House {
	url := h.Property.HdpUrl
	fullAddress := strings.SplitN(url, "/", -1)[2]
	h.Property.Address = strings.ReplaceAll(fullAddress, "-", " ")
	h.Property.MapsUrl = fmt.Sprintf("https://maps.google.com/?q=%s", fullAddress)
	h.Property.FullUrl = fmt.Sprintf("%s%s", base, url)

	for k, v := range h.Property.PriceHistory {
		if v.Event == "Listed for sale" && h.Property.ListPrice == 0 {
			h.Property.ListPrice = v.Price
			h.Property.ListDate = v.Date
			h.Property.PriceHistory = h.Property.PriceHistory[k:]
		}

		// Properties listed for sale aren't sold...
		if d.Sold {
			if v.Event == "Sold" && h.Property.SoldPrice == 0 {
				h.Property.SoldPrice = v.Price
				h.Property.SoldDate = v.Date
				h.Property.PriceHistory = h.Property.PriceHistory[k:]
			}

			if h.Property.ListPrice != 0 && h.Property.SoldPrice != 0 {
				h.Property.PriceDiff = h.Property.SoldPrice - h.Property.ListPrice
				h.Property.PriceDiffPercent = int(float64(h.Property.SoldPrice) / float64(h.Property.ListPrice) * 100)
			}
		}
	}

	return &h
}

// filter applies filters to the house data
func filter(d helpers.Details, h helpers.House) bool {
	if d.Baths > h.Property.ResoFacts.Bathrooms {
		return false
	}

	if d.Beds > h.Property.ResoFacts.Bedrooms {
		return false
	}

	if d.PropertyType != "" && d.PropertyType != h.Property.HomeType {
		return false
	}

	if h.Property.SoldPrice != 0 && d.Price > h.Property.SoldPrice {
		return false
	}

	if h.Property.ListPrice != 0 && d.Price > h.Property.ListPrice {
		return false
	}
	return true
}

// Query scrapes real estate listings from a website
func Query(d helpers.Details) helpers.HouseSlice {
	status := "for_sale/"
	if d.Sold {
		status = "recently_sold/"
	}

	startURL := fmt.Sprintf(`%s/homes/%s%s_rb/`, base, status, d.Location)

	// Visit each page and scrape listings
	c.OnHTML("ul[class*='photo-cards_extra-attribution']", func(e *colly.HTMLElement) {
		e.ForEach("a[data-test='property-card-link']", func(i int, houseElement *colly.HTMLElement) {
			pURL := houseElement.Attr("href")
			c.Visit(pURL)
		})
	})

	c.OnHTML("script#__NEXT_DATA__", func(e *colly.HTMLElement) {
		if !strings.Contains(e.Request.URL.Path, "homedetails") {
			return
		}

		j := helpers.StringToJSON(e.Text)
		h := newDetails(d, *j)

		if !filter(d, *h) {
			return
		}

		houses = append(houses, *h)
	})

	c.Visit(startURL)
	c.Wait()

	if len(houses) == 0 {
		fmt.Println("Property search returned no results")
	}
	return houses
}
