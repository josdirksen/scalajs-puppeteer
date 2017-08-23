module.exports = {
    entry: {
        "example-fastopt": ["/Users/jos/dev/git/workbench-example-app/target/scala-2.12/scalajs-bundler/main/example-fastopt.js"]
    },
    output: {
        filename: "bundle.js" // Output file
    },
    module: {
        loaders: [
            {
                exclude: /node_modules/, // Excluding the `node_modules` folder
                loader: 'babel-loader', // The loader we will be using
                query: {
                    presets: ['es2017'] // Presets for the Babel-Loader
                }
            }
        ]
    },
    target: 'node',
    resolve: {
        extensions: ['.js'] // Extension resolvers
    },
}
