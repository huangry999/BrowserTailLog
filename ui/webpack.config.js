const HtmlWebPackPlugin = require("html-webpack-plugin");
const ExtractTextPlugin = require("extract-text-webpack-plugin");

const extractCSS = new ExtractTextPlugin('stylesheets/[name].css');

module.exports = {
  devtool: 'source-map',
  entry: ["@babel/polyfill", './src/index.js'],
  output: {
    filename: 'log.bundle.js',
    path: __dirname + '/build',
  },
  node: {
    fs: 'empty'
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        use: { loader: "babel-loader" },
      },
      {
        test: /\.html$/,
        use: { loader: "html-loader" },
      },
      {
        test: [/\.less$/i, /\.css$/],
        use: extractCSS.extract([
          { loader: "css-loader" },
          {
            loader: "less-loader",
            options: { javascriptEnabled: true }
          }
        ]),
      },
      {
        test: [/\.bmp$/, /\.gif$/, /\.jpe?g$/, /\.png$/, /\.svg$/],
        loader: require.resolve('url-loader'),
        options: {
          limit: 10000,
          name: 'static/[name].[hash:8].[ext]',
        },
      },
    ]
  },
  plugins: [
    new HtmlWebPackPlugin({
      template: "./public/index.html",
      filename: "./index.html"
    }),
    extractCSS,
  ]
};