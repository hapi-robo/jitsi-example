// https://www.11ty.dev/docs/config/
module.exports = function(config) {
	// Don't process folders with static assets
	config.addPassthroughCopy("assets");

	return {
		passthroughFileCopy: true,
		templateFormats: ["html", "njk"],
		dir: { input: "src", output: '_site' }
	}
}
