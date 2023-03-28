FROM node:18

WORKDIR /usr/src/app

RUN npm i -g pnpm

COPY package.json pnpm-lock.yaml ./

RUN pnpm install --prod --frozen-lockfile

COPY . .

ARG DEBUG=""
ENV DEBUG=${DEBUG}

CMD [ "node", "index.mjs" ]
