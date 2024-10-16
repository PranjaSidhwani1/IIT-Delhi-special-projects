package helpers

// House struct holds scraped data
// Pointers denote that data is not from the JSON and can be empty
type House struct {
	Property struct {
		Description string `json:"description,omitempty"`
		HdpUrl      string `json:"hdpUrl,omitempty"`
		HomeStatus  string `json:"homeStatus,omitempty"`
		HomeType    string `json:"homeType,omitempty"`

		// Non-JSON fields (manually manipulated)
		Address          string `json:"address,omitempty"`
		FullUrl          string `json:"fullUrl,omitempty"`
		MapsUrl          string `json:"mapsURL,omitempty"`
		PriceDiff        int    `json:"priceDiff",omitempty`
		PriceDiffPercent int    `json:"priceDiffPercent,omitempty"`
		ListPrice        int    `json:"listPrice,omitempty"`
		ListDate         string `json:"listDate,omitempty"`
		SoldPrice        int    `json:"soldPrice,omitempty"`
		SoldDate         string `json:"soldDate,omitempty"`

		ResoFacts struct {
			Bathrooms  int    `json:"bathrooms,omitempty"`
			Bedrooms   int    `json:"bedrooms,omitempty"`
			LivingArea string `json:"livingArea,omitempty"`
		} `json:"resoFacts,omitempty"`

		OpenHouseSchedule []struct {
			StartTime string `json:"startTime,omitempty"`
			EndTime   string `json:"endTime,omitempty"`
		} `json:"openHouseSchedule,omitempty"`

		ResponsivePhotos [1]struct {
			Url string `json:"url,omitempty"`
		}

		PriceHistory []struct {
			Date  string `json:"date,omitempty"`
			Event string `json:"event,omitempty"`
			Price int    `json:"price,omitempty"`
		} `json:"priceHistory,omitempty"`
	} `json:"property,omitempty"`
}

// HouseSlice is a slice of House
type HouseSlice []House

// Details struct holds arguments for queries
type Details struct {
	Beds, Baths, Price     int
	Location, PropertyType string
	Sold                   bool
}

const TemplateHTML = `
<!DOCTYPE html>
<html>
  <head>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/tablesort/5.1.0/tablesort.min.js"></script>
    <title>Property Listings in "{{.Location}}" @ {{.At}}</title>
    <style>
      input#myInput {
        width: 220px;
      }

      table#data-table {
        width: 100%;
      }

      th,
      td {
        border: 1px solid #ddd;
        padding: 8px;
        text-align: left;
      }

      th {
        background-color: #f1f1f1;
        cursor: pointer;
      }

      tr:nth-child(even) {
        background-color: #f2f2f2;
      }
    </style>
  </head>

  <body>
    {{- if .Sold}}
    <h1>Sold Property Listings in "{{.Location}}" @ {{.At}}</h1>
    {{- else}}
    <h1>For Sale Property Listings in "{{.Location}}" @ {{.At}}</h1>
    {{- end}}
    <input
      type="text"
      id="myInput"
      onkeyup="myFunction()"
      placeholder="Type to filter results..."
      title="Type in anything to begin filtering..."
    />
    <table id="data-table">
      <thead>
        <tr data-sort-method="none">
          <th>Image</th>
          <th>Address</th>

          <th>Sold Price</th>
          <th>List Price</th>
          <th>Price Diff</th>
          <th>Price Diff %</th>

          <th>Sold Date</th>
          <th>List Date</th>

          <th>Bedrooms</th>
          <th>Bathrooms</th>
          <th>Living Area</th>

          <th>Showing</th>
          <th>Price History</th>

          <th>Home Status</th>
          <th>Home Type</th>
          <th>Description</th>

          <th>Maps URL</th>
        </tr>
      </thead>
      <tbody id="table-body">
        {{range .Houses}}
        <tr>
          <td>
            {{range .Property.ResponsivePhotos}} {{if .Url}}<img
              src="{{.Url}}"
              alt="House Image"
              style="width: 100px; height: 100px"
            />{{else}}{{end}} {{end}}
          </td>
          <td><a href="{{.Property.FullUrl}}">{{.Property.Address}}</a></td>

          <td>
            {{if ne .Property.SoldPrice 0}}${{.Property.SoldPrice}}{{end}}
          </td>
          <td>
            {{if ne .Property.ListPrice 0}}${{.Property.ListPrice}}{{end}}
          </td>
          <td>
            {{if ne .Property.PriceDiff 0}}${{.Property.PriceDiff}}{{end}}
          </td>
          <td>
            {{if ne .Property.PriceDiffPercent
            0}}{{.Property.PriceDiffPercent}}%{{end}}
          </td>

          <td>{{if ne .Property.SoldDate ""}}{{.Property.SoldDate}}{{end}}</td>
          <td>{{if ne .Property.ListDate ""}}{{.Property.ListDate}}{{end}}</td>

          <td>
            {{if ne .Property.ResoFacts.Bedrooms
            0}}{{.Property.ResoFacts.Bedrooms}}{{end}}
          </td>
          <td>
            {{if ne .Property.ResoFacts.Bathrooms
            0}}{{.Property.ResoFacts.Bathrooms}}{{end}}
          </td>
          <td>
            {{if ne .Property.ResoFacts.LivingArea
            ""}}{{.Property.ResoFacts.LivingArea}}{{end}}
          </td>

          <td>
            {{range .Property.OpenHouseSchedule}} {{if
            .StartTime}}{{.StartTime}}{{else}}{{end}} -> {{if
            .EndTime}}{{.EndTime}}{{else}}{{end}}
            <br />
            {{end}}
          </td>

          <td>
            {{range .Property.PriceHistory}} - {{.Event}} on {{.Date}} for {{if
            ne .Price 0}}${{.Price}}{{end}}<br /><br />
            {{end}}
          </td>

          <td>
            {{if ne .Property.HomeStatus ""}}{{.Property.HomeStatus}}{{end}}
          </td>
          <td>{{if ne .Property.HomeType ""}}{{.Property.HomeType}}{{end}}</td>
          <td>
            {{if ne .Property.Description ""}}{{.Property.Description}}{{end}}
          </td>

          <td><a href="{{.Property.MapsUrl}}">Link</a></td>
        </tr>
        {{end}}
      </tbody>
    </table>
    <script>
      function onPageReady() {
        // Documentation: http://tristen.ca/tablesort/demo/
        new Tablesort(document.getElementById("data-table"));
      }

      // Run the above function when the page is loaded & ready
      document.addEventListener("DOMContentLoaded", onPageReady, false);

      const myFunction = () => {
        const trs = document.querySelectorAll("#data-table tr"); // Include all rows
        const filter = document.querySelector("#myInput").value;
        const regex = new RegExp(filter, "i");

        const isFoundInTds = (td) => regex.test(td.textContent); // Use textContent for header
        const isFound = (childrenArr) => childrenArr.some(isFoundInTds);

        const setTrStyleDisplay = ({ style, children }) => {
          if (children[0].tagName === "TH") {
            // If header row
            style.display = ""; // Always show header
          } else {
            style.display = isFound([...children]) ? "" : "none";
          }
        };

        trs.forEach(setTrStyleDisplay);
      };
    </script>
  </body>
</html>
`
