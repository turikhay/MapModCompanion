#!/usr/bin/env python

from logging import DEBUG, INFO, basicConfig, getLogger
from pathlib import Path
from signal import SIGINT
from sys import argv, stdout
from os import environ, makedirs, path
from subprocess import PIPE, STDOUT, run, Popen
from shutil import copytree, rmtree, copyfile
import requests
from yaml import load as yaml_load, dump as yaml_dump, Loader as YamlLoader, Dumper as YamlDumper
from toml import load as toml_load, dump as toml_dump
from datetime import datetime

logger = getLogger("tests_e2e")

PARENT_DIR = Path(path.realpath(__file__)).parent

SERVER_OVERRIDES = {
    'red': 42,
    'blue': 2000,
}

PROXY_OVERRIDES = {
    '42': 1337,
    '2000': 3000,
}

JAVA_DEBUG = {
    'proxy': 9010,
    'red': 9011,
    'blue': 9012,
}

VERSIONS = {
    '1.8.9': {
        'server': '1.8.8',
        'java': 8,
        'world': '1.8.9',
    },
    **(
        dict((
            version,
            {
                'java': 8,
                'world': '1.8.9',
            },
        ) for version in (
            '1.9.4',
            '1.10.2',
            '1.11.2',
            '1.12.2',
        ))
    ),
    **(
        dict((
            version,
            {
                'java': 8,
                'protocollib': True,
                'world': '1.13.2',
            },
        ) for version in (
            '1.13.2',
            '1.14.4',
            '1.15.2',
            '1.16.1',
            '1.16.2',
            '1.16.3',
        ))
    ),
    **(
        dict((
            version,
            {
                'java': 8,
                'world': '1.13.2',
            },
        ) for version in (
            '1.16.4',
            '1.16.5',
        ))
    ),
    **(
        dict((version, {
            'java': 17,
        }) for version in (
            '1.17.1',
            '1.18.2',
            '1.19.3',
            '1.19.4',
            '1.20.1',
            '1.20.2',
            '1.20.3',
        ))
    ),
    **(
        dict((
            version,
            {
                'java': 17,
                'folia': True,
            },
        ) for version in (
            '1.20.4',
        ))
    ),
    **(
        dict((
            version,
            {
                'java': 21,
                'folia': True,
            },
        ) for version in (
            '1.20.6',
            '1.21.4',
        ))
    ),
    **(
        dict((
            version,
            {
                'java': 21,
            },
        ) for version in (
            '1.21.1',
            '1.21.3',
        ))
    ),
}


def gradle_build():
    logger.debug("Running gradle build")
    run(
        ['./gradlew', 'build'],
        check=True,
        cwd=str(PARENT_DIR.parent),
    )


def docker(l: list[str], **kwargs):
    logger.debug(f"Running docker with: {l}")
    p = Popen(
        ['docker', *l],
        cwd=str(PARENT_DIR),
        **kwargs,
    )
    p._sigint_wait_secs = 60.0
    return p


def copy_clean(_from: Path, _to: Path):
    logger.debug(f"Copying {_from} -> {_to}")
    if _to.is_dir():
        rmtree(_to)
    copytree(_from, _to, symlinks=True)


def files_dir_of(entity: str):
    return test_env_dir / f"{entity}"


def copy_plugin(into: Path):
    copyfile(
        PARENT_DIR.parent / "packages" / "single" /
        "build" / "libs" / "MapModCompanion.jar",
        into / "MapModCompanion.jar",
    )


def copy_server_files():
    logger.debug("Copying server files")
    for server_name in servers:
        _to = files_dir_of(server_name)
        copy_clean(
            PARENT_DIR / "server",
            _to,
        )
        copy_plugin(_to / "plugins")
        mmc_config_path = _to / "plugins" / "MapModCompanion" / "config.yml"
        with open(mmc_config_path, "r") as f:
            config = yaml_load(f, Loader=YamlLoader)
        config["overrides"] = {
            'world': SERVER_OVERRIDES[server_name],
        }
        logger.debug(f"Writing config {mmc_config_path}: {config}")
        with open(mmc_config_path, "w") as f:
            yaml_dump(config, f, Dumper=YamlDumper)


def copy_proxy_files():
    logger.debug("Copying proxy files")
    if proxy_type == "waterfall":
        _proxy_dir = "bungeecord"    
    else:
        _proxy_dir = proxy_type
    _from = PARENT_DIR / "proxy" / _proxy_dir
    _to = files_dir_of("proxy")
    copy_clean(
        _from,
        _to,
    )
    if proxy_type == "velocity":
        mmc_config_path = _to / "plugins" / "mapmodcompanion" / "config.toml"
        config = toml_load(mmc_config_path)
        def save(data):
            with open(mmc_config_path, "w") as f:
                toml_dump(data, f)
    else:
        mmc_config_path = _to / "plugins" / "MapModCompanion" / "config.yml"
        with open(mmc_config_path, "r") as f:
            config = yaml_load(f, Loader=YamlLoader)
        def save(data):
            with open(mmc_config_path, "w") as f:
                yaml_dump(data, f, Dumper=YamlDumper)
    config["overrides"] = PROXY_OVERRIDES
    logger.debug(f"Writing config {mmc_config_path}: {config}")
    save(config)
    copy_plugin(_to / "plugins")
    copyfile(
        PARENT_DIR / "proxy" / "Dockerfile",
        _to / "Dockerfile",
    )


if __name__ == "__main__":
    servers = [
        'red',
        'blue',
    ]

    debug_level = 1 # int(environ.get("DEBUG")) if environ.get("DEBUG") else 0
    debug = debug_level > 0
    basicConfig(
        level=DEBUG if debug else INFO
    )

    enable_blue = environ.get("BLUE") == "1"
    if not enable_blue:
        servers.remove('blue')

    java_debug = debug_level or environ.get("JAVA_DEBUG")
    if java_debug:
        logger.info("Java debugging enabled.")
        logger.info("Use 127.0.0.1:9010 for proxy")
        logger.info("Use 127.0.0.1:9011 for red server")
        if enable_blue:
            logger.info("Use 127.0.0.1:9011 for blue server")

    server_type = environ.get("SERVER_TYPE")
    if not server_type:
        server_type = "paper"

    proxy_type, client_version, action = argv[1:]

    version_info = VERSIONS[client_version]

    if server_type == 'folia' and ("folia" not in version_info or not version_info["folia"]):
        logger.info(f"Skipping: Folia is not supported on this version ({client_version})")
        exit(0)

    test_name_suffix = ""
    if server_type == 'folia':
        test_name_suffix += "folia_"
    test_name_suffix += f"{proxy_type}_{client_version}"

    test_name = f"mmc_test_{test_name_suffix}"
    test_env_dir = PARENT_DIR / "test_env" / test_name
    makedirs(test_env_dir, exist_ok=True)

    if debug_level:
        gradle_build()

    copy_server_files()
    copy_proxy_files()

    docker_compose_file_contents = {
        'services': {
        }
    }

    if "bot" not in version_info or version_info["bot"] == True:
        bot_container = test_name
        bot_desc = {
            'container_name': test_name,
            'build': {
                'context': str(PARENT_DIR / "bot"),
                'args': [
                    'DEBUG=minecraft-protocol'
                ] if debug_level > 1 else [
                ]
            },
            'environment': [
                'BOT_HOST=proxy',
                f'BOT_VERSION={client_version}',
            ],
            'depends_on': [
                'proxy'
            ],
        }
        docker_compose_file_contents["services"]["bot"] = bot_desc
    else:
        logger.warning(f"Bot doesn't support {client_version}")
        bot_container = None
        assert action not in ("test",)

    if "server" in version_info:
        server_version = version_info["server"]
    else:
        server_version = client_version

    assert "java" in version_info, f"java version for {server_version} is not defined"
    server_java_version = version_info["java"]

    if "world" in version_info:
        world_version = version_info["world"]
    else:
        world_version = "1.17.1"

    if server_type in ("folia"):
        paper_channel = "experimental"
    else:
        paper_channel = None

    logger.info(f"Server type: {server_type}")

    for server_name in servers:
        server_desc = {
            'build': {
                'context': server_name,
                'args': [
                    f'TAG=java{server_java_version}'
                ],
                'tags': [
                    f'mmc-e2e-server-{server_name}:java{server_java_version}'
                ],
            },
            'environment': [
                f'VERSION={server_version}',
                f'TYPE={server_type.upper()}',
                *([
                    f'PAPER_CHANNEL={paper_channel}',
                ] if paper_channel else []),
            ],
            'ports': [
            ]
        }
        if java_debug:
            server_desc["ports"] += [
                f'{JAVA_DEBUG[server_name]}:9001'
            ]
            server_desc["environment"] += [
                'JVM_XX_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=' +
                '*:9001' if server_java_version > 8 else '9001'
            ]
        docker_compose_file_contents["services"][server_name] = server_desc
        if "protocollib" in version_info and version_info["protocollib"] == True:
            protocollib_source_file = test_env_dir / "ProtocolLib.jar"
            if not protocollib_source_file.is_file():
                logger.info("Downloading ProtocolLib")
                data = requests.get("https://github.com/dmulloy2/ProtocolLib/releases/download/4.8.0/ProtocolLib.jar").content
                try:
                    with open(protocollib_source_file, "wb") as f:
                        f.write(data)
                except Exception as e:
                    protocollib_source_file.unlink(missing_ok=True)
                    raise e
            logger.info("Copying ProtocolLib")
            copyfile(
                protocollib_source_file,
                files_dir_of(server_name) / "plugins" / "ProtocolLib.jar",
            )
        world_dir = files_dir_of(server_name) / "world"
        makedirs(world_dir, exist_ok=True)
        copyfile(
            PARENT_DIR / "saves" / world_version / f"{server_name}.dat",
            world_dir / "level.dat"
        )

    proxy_desc = {
        'build': {
            'context': 'proxy',
        },
        'depends_on': [
            *servers,
        ],
        'environment': [
            f'TYPE={proxy_type}',
        ],
        'ports': [
        ],
    }
    if debug_level:
        proxy_desc["ports"] += [
            '25565:25565',
        ]
    if java_debug:
        proxy_desc["ports"] += [
            f'{JAVA_DEBUG["proxy"]}:9001'
        ]
        proxy_desc["environment"] += [
            'JVM_XX_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9001'
        ]

    if proxy_type in ("waterfall", "bungeecord"):
        bungee_config_path = test_env_dir / "proxy" / "config.yml"
        with open(bungee_config_path, "r") as f:
            bungee_config = yaml_load(f, Loader=YamlLoader)
        bungee_config["servers"] = dict(
            (server_name, {
                'address': f'{server_name}:25565'
            }) for server_name in servers
        )
        with open(bungee_config_path, "w") as f:
            yaml_dump(bungee_config, f, Dumper=YamlDumper)

    if proxy_type in ("velocity"):
        velocity_config_path = test_env_dir / "proxy" / "velocity.toml"
        velocity_config = toml_load(velocity_config_path)
        velocity_config["servers"] = {
            **dict(
                (
                    server_name,
                    f"{server_name}:25565"
                ) for server_name in servers
            ),
            "try": [
                "red",
            ],
        }
        with open(velocity_config_path, "w") as f:
            toml_dump(velocity_config, f)

    docker_compose_file_contents["services"]["proxy"] = proxy_desc

    docker_compose_path = test_env_dir / "docker-compose.yml"

    logger.debug(
        f"Writing {docker_compose_path}: {docker_compose_file_contents}")
    with open(docker_compose_path, "w") as f:
        yaml_dump(docker_compose_file_contents, f, Dumper=YamlDumper)

    auto = bot_container and action in ("test")

    compose = [
        'compose',
        '-f',
        str(docker_compose_path),
    ]

    if action == "build":
        exit_code = docker([
            *compose,
            'build',
            '--no-cache',
        ]).wait()
        exit(exit_code)

    docker_proc = docker([
        *compose,
        'up',
        '--force-recreate',
        '--build',
        *(['-V'] if debug_level else []),
        *(['--detach'] if auto else []),
    ])

    try:
        exit_code = docker_proc.wait()
    except KeyboardInterrupt:
        docker_proc.send_signal(SIGINT)
        exit(1)

    if auto:
        logger.info(f"Waiting for {bot_container}")
        docker_logs = docker(
            [
                *compose,
                'logs',
                *([
                    '-f',
                ] if debug else [
                    '-f',
                    'bot',
                    *servers,
                ])
            ],
            stdout=PIPE,
            stderr=STDOUT,
        )
        docker_wait = docker(
            [
                'wait',
                bot_container,
            ],
            stdout=PIPE,
            stderr=STDOUT,
            text=True
        )

        try:
            while docker_wait.poll() is None:
                ch = docker_logs.stdout.read(1)
                print(datetime.now())
                if ch:
                    stdout.buffer.write(ch)
                    stdout.flush()
        except KeyboardInterrupt:
            docker_logs.kill()

        docker([
            *compose,
            'down',
        ]).wait()

        if docker_wait.returncode != 0 or int(docker_wait.stdout.read()) != 0:
            logger.error("Failed")
            exit(1)

        logger.info("OK")
