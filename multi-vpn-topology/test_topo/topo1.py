from mininet.net import Mininet
from mininet.node import Controller, RemoteController, OVSKernelSwitch
from mininet.link import TCLink
from mininet.log import setLogLevel, info
from mininet.cli import CLI

def myNetwork():
    # Initialize the network
    net = Mininet(topo=None, build=False, ipBase='10.0.0.0/8')

    info('*** Adding controller\n')
    # Add a remote controller
    c0 = net.addController(name='c0',
                           controller=RemoteController,
                           ip='127.0.0.1',
                           protocol='tcp',
                           port=6653)

    info('*** Add switches\n')
    s1 = net.addSwitch('s1', cls=OVSKernelSwitch, protocols='OpenFlow13')
    s2 = net.addSwitch('s2', cls=OVSKernelSwitch, protocols='OpenFlow13')
    s3 = net.addSwitch('s3', cls=OVSKernelSwitch, protocols='OpenFlow13')
    s4 = net.addSwitch('s4', cls=OVSKernelSwitch, protocols='OpenFlow13')

    info('*** Add hosts\n')
    h1 = net.addHost('h1', ip='10.0.0.1', mac='00:00:00:00:00:01', defaultRoute=None)
    h2 = net.addHost('h2', ip='10.0.0.2', mac='00:00:00:00:00:02', defaultRoute=None)

    info('*** Add links\n')
    # Add links with bandwidth settings
    h1s1 = {'bw': 100}
    net.addLink(h1, s1, cls=TCLink, **h1s1)

    h2s2 = {'bw': 100}
    net.addLink(h2, s2, cls=TCLink, **h2s2)

    s1s4 = {'bw': 100}
    net.addLink(s1, s4, cls=TCLink, **s1s4)

    s1s3 = {'bw': 100}
    net.addLink(s1, s3, cls=TCLink, **s1s3)

    s3s2 = {'bw': 100}
    net.addLink(s3, s2, cls=TCLink, **s3s2)

    s4s2 = {'bw': 100}
    net.addLink(s4, s2, cls=TCLink, **s4s2)

    info('*** Starting network\n')
    net.build()

    info('*** Starting controllers\n')
    for controller in net.controllers:
        controller.start()

    info('*** Starting switches\n')
    net.get('s1').start([c0])
    net.get('s2').start([c0])
    net.get('s3').start([c0])
    net.get('s4').start([c0])

   # disable certain links if needed
   # net.configLinkStatus('s1', 's4', 'down')

    info('*** Post configure switches and hosts\n')
    CLI(net)  # Launch CLI for interaction

    info('*** Stopping network\n')
    net.stop()

if __name__ == '__main__':
    setLogLevel('info')  # Set log level to info for better debugging
    myNetwork()

